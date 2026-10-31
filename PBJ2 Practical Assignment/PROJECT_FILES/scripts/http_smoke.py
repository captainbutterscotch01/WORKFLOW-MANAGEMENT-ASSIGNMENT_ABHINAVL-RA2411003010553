import urllib.request,urllib.error,json,datetime,pathlib
root=pathlib.Path(__file__).resolve().parents[1]
base='http://localhost:8090';records=[]
def call(path,body=None,expected=200):
 data=json.dumps(body).encode() if body is not None else None
 req=urllib.request.Request(base+path,data=data,headers={'Content-Type':'application/json'},method='POST' if body is not None else 'GET')
 try:
  with urllib.request.urlopen(req,timeout=10) as r:code=r.status;out=json.load(r)
 except urllib.error.HTTPError as e:code=e.code;out=json.loads(e.read())
 assert code==expected,(path,code,out)
 records.append({'path':path,'request':body,'status':code,'response':out,'assertion':'PASS'});return out
call('/mock/health')
for service,request in [('credit',{'caseId':'HTTP-CREDIT','creditProfile':'good'}),('payment',{'caseId':'HTTP-PAYMENT','paymentToken':'tok_success','orderTotal':1000}),('scheduling',{'caseId':'HTTP-SCHEDULING','appointmentPreference':'Morning','slotScenario':'available'})]:
 path='/mock/'+service;out=call(path,request)
 call('/mock/faults/'+service+'/'+request['caseId'],{'enabled':True});call(path,request,503)
 call('/mock/faults/'+service+'/'+request['caseId'],{'enabled':False});assert call(path,request)==out
assert call('/mock/payment',{'caseId':'HTTP-DECLINED','paymentToken':'tok_decline','orderTotal':1000})['paymentStatus']=='Declined'
assert call('/mock/scheduling',{'caseId':'HTTP-NONE','appointmentPreference':'Afternoon','slotScenario':'none'})['availableSlots']==[]
r={'caseId':'HTTP-BOOK','selectedSlot':'SYN-MORNING-001'};a=call('/mock/bookings',r);assert call('/mock/bookings',r)==a
(root/'evidence/http-smoke-results.json').write_text(json.dumps({'executedAt':datetime.datetime.now(datetime.timezone.utc).isoformat(),'scope':'Actual HTTP calls to locally running Spring Boot mock API; Camunda client disabled. NOT workflow/connector execution.','checks':records},indent=2))
print('PASS:',len(records),'real HTTP responses checked')
