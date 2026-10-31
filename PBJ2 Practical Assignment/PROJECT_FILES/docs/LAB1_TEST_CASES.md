# Lab 1 - workflow execution test matrix

Live execution was performed on a Windows/Docker installation on 30-31 October 2026. Results below were transcribed from Operate/Tasklist screenshots and copied process variables. Fifteen required scenarios have observed terminal outcomes. Seven additional example scenarios are not claimed as live-tested. Screenshot times are shown as displayed on the test PC.

| Case / input file | Expected route/result | Actual outcome / instance key | Status |
|---|---|---|---|
| `L1-approved`; `examples/L1-approved.json` | Approved | Approved; instance 2251799813686796 | Passed |
| `L2-rejected`; `examples/L2-rejected.json` | Rejected | Rejected; copied variables plus completed-list evidence; instance 2251799813691464 | Passed |
| `L3-manual`; `examples/L3-manual.json` | Manual loan review; Approved after reviewer approval | Manual Review -> reviewer Approve -> Approved; instance 2251799813692297 | Passed |
| `L4-credit-error`; `examples/L4-credit-error.json` | Credit connector incident; Approved after fault cleared and retry | Credit incident -> correction/retry -> Approved; instance 2251799813700233 | Passed |
| `L5-worker-error`; `examples/L5-worker-error.json` | Validation worker incident; Approved after fault cleared and retry | Validation worker incident -> correction/retry -> Approved; instance 2251799813701734 | Passed |
| `L6-invalid`; `examples/L6-invalid.json` | Invalid application | Not run; optional additional example; instance None | Not run (optional) |
| `L7-manual-reject`; `examples/L7-manual-reject.json` | Manual loan review; Rejected after reviewer rejection | Not run; optional additional example; instance None | Not run (optional) |

Full input values are in the referenced JSON files. Failure examples require enabling their named mock fault before start.

H5: the incident screenshot is a Lab 3 dashboard view without an instance key. The recovered H5 instance is 2251799813723383. Their association follows the supplied test sequence and user-reported correction/retry, not an independently visible before/after key match. No detailed incident screenshot was supplied. Tasklist captures include pre-completion forms/lists; final human outcomes are supported by Operate history and copied variables. The L3 form belongs to an active duplicate, while the completed L3 evidence uses a different instance. L2 rejection is supported by copied variables and the completed instance list rather than a dedicated final detail image.
