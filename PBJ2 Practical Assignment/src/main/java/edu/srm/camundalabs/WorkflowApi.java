package edu.srm.camundalabs;
import java.io.IOException;
import java.util.*;
import io.camunda.client.CamundaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api")
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="labs.workflows-enabled",havingValue="true",matchIfMissing=true)
public class WorkflowApi {
 private final CamundaClient client; private final String connectorBase;
 public WorkflowApi(CamundaClient client,@Value("${labs.connector-base-url}") String connectorBase) { this.client=client;this.connectorBase=connectorBase; }
 @PostMapping("/deploy") public Map<String,Object> deploy() throws IOException {
  var cmd=client.newDeployResourceCommand().addResourceFromClasspath("models/lab1-loan.bpmn");
  for(var r:new PathMatchingResourcePatternResolver().getResources("classpath*:models/*")) {
   String name=r.getFilename(); if(name!=null && !name.equals("lab1-loan.bpmn") && (name.endsWith(".bpmn")||name.endsWith(".dmn")||name.endsWith(".form"))) try(var in=r.getInputStream()) {cmd.addResourceBytes(in.readAllBytes(),name);}
  }
  var result=cmd.send().join();return Map.of("deploymentKey",String.valueOf(result.getKey()),"processCount",result.getProcesses().size());
 }
 @PostMapping("/start/{lab}") public Map<String,String> start(@PathVariable String lab,@RequestBody Map<String,Object> body) {
  String process=switch(lab) {case "loan"->"Lab1_Loan";case "order"->"Lab2_Order";case "hospital"->"Lab3_Hospital";default->throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Use loan, order or hospital");};
  var v=new HashMap<>(body);
  if(LabLogic.str(v,"caseId").isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"caseId is required; use a unique value per independent case");
  v.putIfAbsent("reviewer","demo");v.put("mockBaseUrl",connectorBase);
  var r=client.newCreateInstanceCommand().bpmnProcessId(process).latestVersion().variables(v).send().join();
  return Map.of("processInstanceKey",String.valueOf(r.getProcessInstanceKey()),"processId",process,"caseId",LabLogic.str(v,"caseId"));
 }
}
