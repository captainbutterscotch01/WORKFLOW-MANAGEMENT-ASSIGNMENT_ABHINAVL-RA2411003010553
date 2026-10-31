package edu.srm.camundalabs;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.camunda.bpm.dmn.engine.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
class LabTests {
 private final DmnEngine engine=DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
 private Object decision(String file,String id,Map<String,Object> vars) {
  try(var in=getClass().getResourceAsStream("/models/"+file)) {
   var d=engine.parseDecision(id,in); return engine.evaluateDecisionTable(d,vars).getSingleResult().getSingleEntry();
  }catch(Exception e){throw new RuntimeException(e);}
 }
 @ParameterizedTest @CsvSource({"30,50000,780,300000,Approved","20,50000,780,300000,Rejected","30,19000,780,100000,Rejected","30,50000,500,300000,Rejected","30,50000,780,1250000,Rejected","30,50000,650,300000,Manual Review","21,30000,750,360000,Approved","21,20000,600,480000,Manual Review","21,20000,600,480001,Rejected"})
 void loanDecision(int age,int income,int score,int amount,String expected) {assertEquals(expected,decision("lab1-loan.dmn","LoanDecision",Map.of("age",age,"monthlyIncome",income,"creditScore",score,"loanAmount",amount)));}
 @ParameterizedTest @CsvSource({"1000,Standard,Standard,Normal","1000,VIP,Standard,High","1000,Standard,Express,High","10000,Standard,Standard,Medium","49999,Standard,Standard,Medium","50000,Standard,Standard,High"})
 void orderPriority(int total,String category,String delivery,String expected) {assertEquals(expected,decision("lab2-priority.dmn","OrderPriority",Map.of("orderTotal",total,"customerCategory",category,"deliveryPreference",delivery)));}
 @ParameterizedTest @CsvSource({"false,false,Routine","false,true,Priority Review","true,false,Urgent Assessment","true,true,Urgent Assessment"})
 void triage(boolean urgent,boolean priority,String expected) {assertEquals(expected,decision("lab3-triage.dmn","HospitalTriage",Map.of("urgentIndicator",urgent,"priorityIndicator",priority)));}
 @Test void camunda8BpmnSchema() {for(String file:List.of("lab1-loan.bpmn","lab2-order.bpmn","lab3-hospital.bpmn")){var m=io.camunda.zeebe.model.bpmn.Bpmn.readModelFromStream(getClass().getResourceAsStream("/models/"+file));io.camunda.zeebe.model.bpmn.Bpmn.validateModel(m);}}
 @Test void bpmnSchema() {for(String file:List.of("lab1-loan.bpmn","lab2-order.bpmn","lab3-hospital.bpmn")){var m=Bpmn.readModelFromStream(getClass().getResourceAsStream("/models/"+file));Bpmn.validateModel(m);}}
 static Map<String,Object> loan() {return new HashMap<>(Map.of("caseId","L1","applicantRef","SYN-A1","applicantName","Synthetic Applicant","age",30,"monthlyIncome",50000,"loanAmount",300000,"creditProfile","good"));}
 static Map<String,Object> order() {return new HashMap<>(Map.of("caseId","O1","customerRef","SYN-C1","productId","P100","quantity",2,"customerCategory","Standard","deliveryPreference","Standard","paymentToken","tok_success"));}
 static Map<String,Object> hospital() {return new HashMap<>(Map.of("caseId","H1","patientRef","SYN-P1","symptomsSummary","Synthetic scenario","appointmentPreference","Morning","urgentIndicator",false,"priorityIndicator",false,"slotScenario","available"));}
 @Test void validLoan(){assertEquals(true,LabLogic.loan(loan()).get("validationPassed"));}
 @Test void missingName(){var v=loan();v.remove("applicantName");assertEquals(false,LabLogic.loan(v).get("validationPassed"));}
 @Test void zeroIncome(){var v=loan();v.put("monthlyIncome",0);assertEquals(false,LabLogic.loan(v).get("validationPassed"));}
 @Test void validOrder(){var r=LabLogic.order(order());assertEquals(true,r.get("stockAvailable"));assertEquals(new java.math.BigDecimal("1000"),r.get("orderTotal"));}
 @Test void outOfStock(){var v=order();v.put("productId","P300");assertEquals(false,LabLogic.order(v).get("stockAvailable"));}
 @Test void fractionalQuantity(){var v=order();v.put("quantity",1.5);assertEquals(false,LabLogic.order(v).get("orderValid"));}
 @Test void unknownProduct(){var v=order();v.put("productId","MISSING");assertEquals(false,LabLogic.order(v).get("orderValid"));}
 @Test void highOrder(){var v=order();v.put("productId","P200");assertEquals(new java.math.BigDecimal("60000"),LabLogic.order(v).get("orderTotal"));}
 @Test void validHospital(){assertEquals(true,LabLogic.registration(hospital()).get("registrationValid"));}
 @Test void indicatorsMustBeBoolean(){var v=hospital();v.put("urgentIndicator","false");assertEquals(false,LabLogic.registration(v).get("registrationValid"));}
 @Test void workerFaultRecovery(){Faults f=new Faults();f.set("loan-worker","L1",true);assertThrows(IllegalStateException.class,()->f.check("loan-worker","L1"));f.set("loan-worker","L1",false);assertDoesNotThrow(()->f.check("loan-worker","L1"));}
 @Test void inventoryFaultRecovery(){Faults f=new Faults();f.set("inventory","O1",true);assertThrows(IllegalStateException.class,()->f.check("inventory","O1"));f.set("inventory","O1",false);assertDoesNotThrow(()->f.check("inventory","O1"));}
 @Test void creditHttpFailureAndRecovery() throws Exception {
  Faults f=new Faults();MockMvc mvc=MockMvcBuilders.standaloneSetup(new MockApi(f)).build();String body="{\"caseId\":\"L1\",\"creditProfile\":\"good\"}";
  f.set("credit","L1",true);mvc.perform(post("/mock/credit").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isServiceUnavailable());
  f.set("credit","L1",false);mvc.perform(post("/mock/credit").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.score").value(780));
 }
 @Test void paymentDeclineAndIdempotency() {
  MockApi api=new MockApi(new Faults());var request=new HashMap<String,Object>(Map.of("caseId","O1","orderTotal",1000,"paymentToken","tok_success"));var first=api.payment(request);assertEquals("Paid",first.get("paymentStatus"));assertEquals(first,api.payment(request));request.put("caseId","O2");request.put("paymentToken","tok_decline");assertEquals("Declined",api.payment(request).get("paymentStatus"));
 }
 @Test void paymentHttpFailureAndRecovery() throws Exception {
  Faults f=new Faults();MockMvc mvc=MockMvcBuilders.standaloneSetup(new MockApi(f)).build();String body="{\"caseId\":\"O1\",\"orderTotal\":1000,\"paymentToken\":\"tok_success\"}";
  f.set("payment","O1",true);mvc.perform(post("/mock/payment").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isServiceUnavailable());f.set("payment","O1",false);mvc.perform(post("/mock/payment").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.paymentStatus").value("Paid"));
 }
 @Test void schedulingFailureAndRecovery() throws Exception {
  Faults f=new Faults();MockMvc mvc=MockMvcBuilders.standaloneSetup(new MockApi(f)).build();String body="{\"caseId\":\"H1\",\"appointmentPreference\":\"Morning\",\"slotScenario\":\"available\"}";
  f.set("scheduling","H1",true);mvc.perform(post("/mock/scheduling").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isServiceUnavailable());f.set("scheduling","H1",false);mvc.perform(post("/mock/scheduling").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.slotsAvailable").value(true));
 }
 @Test void noSlots(){assertEquals(false,new MockApi(new Faults()).slots(Map.of("appointmentPreference","Morning","slotScenario","none")).get("slotsAvailable"));}
 @Test void bookingIdempotency(){var api=new MockApi(new Faults());var r=Map.<String,Object>of("caseId","H1","selectedSlot","SYN-MORNING-001");assertEquals("Booked",api.book(r).get("bookingStatus"));assertEquals(api.book(r),api.book(r));}
}
