package edu.srm.camundalabs;
import java.math.BigDecimal;
import java.util.*;
/** Deterministic, synthetic educational rules; no financial or medical advice. */
public final class LabLogic {
 private LabLogic() {}
 public static String str(Map<String,Object> v,String k) { return Objects.toString(v.get(k), "").trim(); }
 public static BigDecimal number(Map<String,Object> v,String k) {
  try { return new BigDecimal(str(v,k)); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
 }
 public static boolean integer(Map<String,Object> v,String k) {
  try { return new BigDecimal(str(v,k)).stripTrailingZeros().scale() <= 0; } catch(Exception e) { return false; }
 }
 public static Map<String,Object> loan(Map<String,Object> v) {
  List<String> errors=new ArrayList<>();
  for(String k:List.of("caseId","applicantRef","applicantName")) if(str(v,k).isBlank()) errors.add(k+" is required");
  if(!(v.get("age") instanceof Number) || !integer(v,"age") || number(v,"age").signum()<=0) errors.add("age must be a positive integer");
  for(String k:List.of("loanAmount","monthlyIncome")) if(!(v.get(k) instanceof Number) || number(v,k).signum()<=0) errors.add(k+" must be positive");
  if(!Set.of("good","poor","borderline").contains(str(v,"creditProfile"))) errors.add("unsupported creditProfile");
  return Map.of("validationPassed",errors.isEmpty(),"validationErrors",errors);
 }
 public record Product(BigDecimal price,int stock) {}
 public static final Map<String,Product> CATALOG=Map.of("P100",new Product(new BigDecimal("500"),20),"P200",new Product(new BigDecimal("30000"),10),"P300",new Product(new BigDecimal("1500"),0));
 public static Map<String,Object> order(Map<String,Object> v) {
  List<String> errors=new ArrayList<>();
  for(String k:List.of("caseId","customerRef","paymentToken")) if(str(v,k).isBlank()) errors.add(k+" is required");
  Product p=CATALOG.get(str(v,"productId"));
  if(p==null) errors.add("unknown productId");
  BigDecimal quantity=number(v,"quantity");
  if(!(v.get("quantity") instanceof Number) || !integer(v,"quantity") || quantity.signum()<=0 || quantity.compareTo(new BigDecimal("1000"))>0) errors.add("quantity must be integer 1..1000");
  if(!Set.of("Standard","VIP").contains(str(v,"customerCategory"))) errors.add("customerCategory must be Standard or VIP");
  if(!Set.of("Standard","Express").contains(str(v,"deliveryPreference"))) errors.add("deliveryPreference must be Standard or Express");
  boolean valid=errors.isEmpty();
  return Map.of("orderValid",valid,"validationErrors",errors,"stockAvailable",valid && quantity.intValue()<=p.stock(),"orderTotal",valid?p.price().multiply(quantity):BigDecimal.ZERO);
 }
 public static Map<String,Object> registration(Map<String,Object> v) {
  List<String> errors=new ArrayList<>();
  for(String k:List.of("caseId","patientRef","symptomsSummary")) if(str(v,k).isBlank()) errors.add(k+" is required");
  if(!Set.of("Morning","Afternoon").contains(str(v,"appointmentPreference"))) errors.add("appointmentPreference must be Morning or Afternoon");
  for(String k:List.of("urgentIndicator","priorityIndicator")) if(!(v.get(k) instanceof Boolean)) errors.add(k+" must be boolean");
  if(!Set.of("available","none").contains(str(v,"slotScenario"))) errors.add("slotScenario must be available or none");
  return Map.of("registrationValid",errors.isEmpty(),"validationErrors",errors);
 }
}
