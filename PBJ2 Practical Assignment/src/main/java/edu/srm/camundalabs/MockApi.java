package edu.srm.camundalabs;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/mock")
public class MockApi {
 private final Faults faults;
 private final Map<String,Map<String,Object>> payments=new ConcurrentHashMap<>(), bookings=new ConcurrentHashMap<>();
 public MockApi(Faults faults) { this.faults=faults; }
 private void check(String service,Map<String,Object> v) { if(faults.active(service,LabLogic.str(v,"caseId"))) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Injected "+service+" failure"); }
 @GetMapping("/health") public Map<String,String> health() { return Map.of("status","UP"); }
 @PostMapping("/faults/{service}/{caseId}") public Map<String,Object> fault(@PathVariable String service,@PathVariable String caseId,@RequestBody Map<String,Boolean> body) {
  if(!Set.of("loan-worker","inventory","credit","payment","scheduling").contains(service)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown fault service");
  boolean enabled=Boolean.TRUE.equals(body.get("enabled")); faults.set(service,caseId,enabled); return Map.of("service",service,"caseId",caseId,"enabled",enabled);
 }
 @PostMapping("/credit") public Map<String,Object> credit(@RequestBody Map<String,Object> v) {
  check("credit",v);
  int score=switch(LabLogic.str(v,"creditProfile")) { case "good"->780; case "borderline"->650; case "poor"->500; default->throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown profile"); };
  return Map.of("score",score,"assessmentStatus","OK");
 }
 @PostMapping("/payment") public Map<String,Object> payment(@RequestBody Map<String,Object> v) {
  check("payment",v); String key=LabLogic.str(v,"caseId"),token=LabLogic.str(v,"paymentToken");
  if(key.isBlank() || LabLogic.number(v,"orderTotal").signum()<=0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"caseId and positive orderTotal required");
  if(!Set.of("tok_success","tok_decline").contains(token)) return Map.of("paymentStatus","Declined","reason","Unknown synthetic token");
  return payments.computeIfAbsent(key,k->Map.of("paymentStatus",token.equals("tok_success")?"Paid":"Declined","transactionRef","SYN-PAY-"+k));
 }
 @PostMapping("/scheduling") public Map<String,Object> slots(@RequestBody Map<String,Object> v) {
  check("scheduling",v);
  String pref=LabLogic.str(v,"appointmentPreference");
  if(!Set.of("Morning","Afternoon").contains(pref)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid appointmentPreference");
  List<String> slots="none".equals(LabLogic.str(v,"slotScenario"))?List.of():List.of("SYN-"+pref.toUpperCase()+"-001","SYN-"+pref.toUpperCase()+"-002");
  return Map.of("availableSlots",slots,"slotsAvailable",!slots.isEmpty());
 }
 @PostMapping("/bookings") public Map<String,Object> book(@RequestBody Map<String,Object> v) {
  check("scheduling",v);
  String key=LabLogic.str(v,"caseId"),slot=LabLogic.str(v,"selectedSlot");
  if(key.isBlank() || !Set.of("SYN-MORNING-001","SYN-MORNING-002","SYN-AFTERNOON-001","SYN-AFTERNOON-002").contains(slot)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Valid synthetic slot and caseId required");
  return bookings.computeIfAbsent(key,k->Map.of("bookingStatus","Booked","bookingReference","SYN-BOOK-"+k,"selectedSlot",slot));
 }
}
