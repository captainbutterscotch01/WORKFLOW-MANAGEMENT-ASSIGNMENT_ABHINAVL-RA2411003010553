param([Parameter(Mandatory=$true)][string]$Service,[Parameter(Mandatory=$true)][string]$CaseId,[string]$BaseUrl="http://localhost:8090")
$ErrorActionPreference="Stop"
Invoke-RestMethod -Method Post -Uri "$BaseUrl/mock/faults/$Service/$CaseId" -ContentType "application/json" -Body '{"enabled":false}'
Write-Host "Underlying mock fault cleared. In Operate retry the incident with retries set to at least 1."
