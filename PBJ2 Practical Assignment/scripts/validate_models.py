"""Standard-library structural checks; does not replace an engine deployment."""
from pathlib import Path
import json,xml.etree.ElementTree as E
root=Path(__file__).resolve().parents[1]/'src/main/resources/models'
b='http://www.omg.org/spec/BPMN/20100524/MODEL';z='http://camunda.org/schema/zeebe/1.0';d='https://www.omg.org/spec/DMN/20191111/MODEL/'
decisions={n.attrib['id'] for f in root.glob('*.dmn') for n in E.parse(f).iter('{'+d+'}decision')}
forms={json.loads(f.read_text())['id'] for f in root.glob('*.form')}
for f in root.glob('*.bpmn'):
 t=E.parse(f);proc=t.find('{'+b+'}process');ids={e.get('id') for e in t.iter() if e.get('id')};assert len(ids)==len([e for e in t.iter() if e.get('id')]),'duplicate ID'
 nodes={e.get('id'):e for e in proc if e.get('id')};flows={e.get('id'):e for e in proc.findall('{'+b+'}sequenceFlow')}
 for flow in flows.values():assert flow.get('sourceRef') in nodes and flow.get('targetRef') in nodes
 for g in proc.findall('{'+b+'}exclusiveGateway'):
  outgoing=[f for f in flows.values() if f.get('sourceRef')==g.get('id')]
  if len(outgoing)>1:
   assert g.get('default') in flows
   for f2 in outgoing:assert (f2.find('{'+b+'}conditionExpression') is not None)==(f2.get('id')!=g.get('default'))
 for e in t.iter('{'+z+'}calledDecision'):assert e.get('decisionId') in decisions and e.get('bindingType')=='deployment'
 for e in t.iter('{'+z+'}formDefinition'):assert e.get('formId') in forms and e.get('bindingType')=='deployment'
 start=proc.find('{'+b+'}startEvent').get('id');seen={start};pending=[start]
 while pending:
  for flow in flows.values():
   if flow.get('sourceRef')==pending[0] and flow.get('targetRef') not in seen:seen.add(flow.get('targetRef'));pending.append(flow.get('targetRef'))
  pending.pop(0)
 for e in proc:
  if e.get('id') and e.tag!='{'+b+'}sequenceFlow':assert e.get('id') in seen,'unreachable node'
 print(f.name+': references, reachability, gateway conditions and deployment bindings PASS')
for f in root.glob('*.form'):
 form=json.loads(f.read_text());keys=[c['key'] for c in form['components'] if 'key' in c];assert len(keys)==len(set(keys));print(f.name+': unique form keys PASS')
