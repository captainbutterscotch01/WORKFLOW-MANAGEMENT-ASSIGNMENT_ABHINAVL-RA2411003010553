"""Start real Camunda instances; writes real keys, never invents outcomes."""
import json, pathlib, urllib.request, datetime, sys
root=pathlib.Path(__file__).resolve().parents[1]
base=sys.argv[1] if len(sys.argv)>1 else 'http://localhost:8090'
def post(path,data=None):
 r=urllib.request.Request(base+path,data=json.dumps(data or {}).encode(),headers={'Content-Type':'application/json'},method='POST')
 with urllib.request.urlopen(r,timeout=60) as response:return json.load(response)
out=root/'evidence/live';out.mkdir(parents=True,exist_ok=True)
(out/'deployment.json').write_text(json.dumps(post('/api/deploy'),indent=2))
results=[];stamp=datetime.datetime.now().strftime('%Y%m%d%H%M%S')
for case in json.loads((root/'examples/scenarios.json').read_text()):
 v=case['variables'];v['caseId']+='-'+stamp
 if case['faultService']:post('/mock/faults/'+case['faultService']+'/'+v['caseId'],{'enabled':True})
 r=post('/api/start/'+case['lab'],v)
 results.append(dict(caseId=v['caseId'],lab=case['lab'],processInstanceKey=r['processInstanceKey'],faultService=case['faultService'],expected=case['expected'],actual='Inspect Operate / Tasklist',status='STARTED; outcome not verified'))
 (out/'started-cases.json').write_text(json.dumps(results,indent=2));print(v['caseId'],r['processInstanceKey'])
