param([string]$BaseUrl="http://localhost:8090")
$ErrorActionPreference="Stop"
$root=Split-Path $PSScriptRoot -Parent
New-Item -ItemType Directory -Force "$root/evidence/live" | Out-Null
Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/deploy" | ConvertTo-Json -Depth 10 | Set-Content "$root/evidence/live/deployment.json"
$cases=Get-Content "$root/examples/scenarios.json" -Raw | ConvertFrom-Json
$stamp=Get-Date -Format "yyyyMMddHHmmss"
$results=@()
foreach($case in $cases) {
 $id="$($case.id)-$stamp"
 $case.variables.caseId=$id
 if($case.faultService) {
  Invoke-RestMethod -Method Post -Uri "$BaseUrl/mock/faults/$($case.faultService)/$id" -ContentType "application/json" -Body '{"enabled":true}' | Out-Null
 }
 $r=Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/start/$($case.lab)" -ContentType "application/json" -Body ($case.variables | ConvertTo-Json -Depth 10)
 $results += [PSCustomObject]@{caseId=$id;lab=$case.lab;processInstanceKey=$r.processInstanceKey;faultService=$case.faultService;expected=$case.expected;actual="Inspect Operate / Tasklist";status="STARTED; outcome not verified"}
 $results | ConvertTo-Json -Depth 10 | Set-Content "$root/evidence/live/started-cases.json"
 Write-Host "$id : $($r.processInstanceKey)"
}
Write-Host "Instances started. Complete human tasks and perform incident recovery using docs/EVIDENCE_GUIDE.md."
