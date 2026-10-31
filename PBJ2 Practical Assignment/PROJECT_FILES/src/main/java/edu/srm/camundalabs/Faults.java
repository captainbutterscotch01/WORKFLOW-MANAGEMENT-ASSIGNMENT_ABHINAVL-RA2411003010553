package edu.srm.camundalabs;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
@Component
public class Faults {
 private final Set<String> faults=ConcurrentHashMap.newKeySet();
 public void set(String service,String caseId,boolean enabled) { if(enabled) faults.add(service+":"+caseId); else faults.remove(service+":"+caseId); }
 public boolean active(String service,String caseId) { return faults.contains(service+":"+caseId); }
 public void check(String service,String caseId) { if(active(service,caseId)) throw new IllegalStateException("Injected "+service+" failure for "+caseId+". Clear fault and retry incident."); }
}
