package edu.srm.camundalabs;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="labs.workflows-enabled",havingValue="true",matchIfMissing=true)
public class Workers {
 private final Faults faults; private final RestClient http;
 public Workers(Faults faults,@Value("${labs.mock-base-url}") String base) {
  this.faults=faults;
  var f=new SimpleClientHttpRequestFactory(); f.setConnectTimeout(Duration.ofSeconds(5)); f.setReadTimeout(Duration.ofSeconds(10));
  http=RestClient.builder().baseUrl(base).requestFactory(f).build();
 }
 private void run(JobClient client,ActivatedJob job,Supplier<Map<String,Object>> action) {
  Map<String,Object> result;
  try { result=action.get(); }
  catch(Exception e) { client.newFailCommand(job.getKey()).retries(0).errorMessage(e.getClass().getSimpleName()+": "+e.getMessage()).send().join(); return; }
  client.newCompleteCommand(job.getKey()).variables(result).send().join();
 }
 @JobWorker(type="record-outcome",autoComplete=false)
 public void outcome(JobClient c,ActivatedJob j) { run(c,j,()->Map.of("outcome",LabLogic.str(j.getVariablesAsMap(),"finalStatus"))); }
 @JobWorker(type="loan-validate",autoComplete=false)
 public void loan(JobClient c,ActivatedJob j) { run(c,j,()->{var v=j.getVariablesAsMap();faults.check("loan-worker",LabLogic.str(v,"caseId"));return LabLogic.loan(v);}); }
 @JobWorker(type="order-validate",autoComplete=false)
 public void order(JobClient c,ActivatedJob j) { run(c,j,()->{var v=j.getVariablesAsMap();faults.check("inventory",LabLogic.str(v,"caseId"));return LabLogic.order(v);}); }
 @JobWorker(type="hospital-register",autoComplete=false)
 public void register(JobClient c,ActivatedJob j) { run(c,j,()->LabLogic.registration(j.getVariablesAsMap())); }
 @SuppressWarnings("unchecked")
 @JobWorker(type="hospital-availability",autoComplete=false)
 public void availability(JobClient c,ActivatedJob j) { run(c,j,()->Map.of("preliminaryAvailability",Objects.requireNonNull(http.post().uri("/mock/scheduling").body(j.getVariablesAsMap()).retrieve().body(Map.class)))); }
 @SuppressWarnings("unchecked")
 @JobWorker(type="hospital-book",autoComplete=false)
 public void booking(JobClient c,ActivatedJob j) { run(c,j,()->{
  var v=new HashMap<>(j.getVariablesAsMap()); var slots=(List<String>)v.get("availableSlots");
  if(slots==null || slots.isEmpty()) throw new IllegalStateException("No slot available for booking");
  v.put("selectedSlot",slots.get(0)); return Objects.requireNonNull(http.post().uri("/mock/bookings").body(v).retrieve().body(Map.class));
 }); }
}
