# Lab 3 - workflow execution test matrix

Live execution was performed on a Windows/Docker installation on 30-31 October 2026. Results below were transcribed from Operate/Tasklist screenshots and copied process variables. Fifteen required scenarios have observed terminal outcomes. Seven additional example scenarios are not claimed as live-tested. Screenshot times are shown as displayed on the test PC.

| Case / input file | Expected route/result | Actual outcome / instance key | Status |
|---|---|---|---|
| `H1-routine`; `examples/H1-routine.json` | Booked | Booked; instance 2251799813697163 | Passed |
| `H2-priority`; `examples/H2-priority.json` | Priority staff review; Booked after continuation | Priority staff review -> continuation -> Booked; instance 2251799813716434 | Passed |
| `H3-urgent`; `examples/H3-urgent.json` | Immediate human assessment; Human assessment recorded after completion | Human assessment recorded; staffActionRecorded=true; instance 2251799813717405 | Passed |
| `H4-no-slots`; `examples/H4-no-slots.json` | No-slot staff task; Staff follow-up required after completion | No slots; staff review -> Staff follow-up required; instance 2251799813717956 | Passed |
| `H5-scheduling-error`; `examples/H5-scheduling-error.json` | Availability worker incident; Booked after fault cleared and retry | Lab 3 HTTP 503 dashboard observed; user reported correction/retry; H5 completed Booked; instance 2251799813723383 | Passed with evidence limitation |
| `H6-priority-refer`; `examples/H6-priority-refer.json` | Priority staff review; Referred to staff | Not run; optional additional example; instance None | Not run (optional) |
| `H7-invalid`; `examples/H7-invalid.json` | Invalid registration | Not run; optional additional example; instance None | Not run (optional) |

Full input values are in the referenced JSON files. Failure examples require enabling their named mock fault before start.

H5: the incident screenshot is a Lab 3 dashboard view without an instance key. The recovered H5 instance is 2251799813723383. Their association follows the supplied test sequence and user-reported correction/retry, not an independently visible before/after key match. No detailed incident screenshot was supplied. Tasklist captures include pre-completion forms/lists; final human outcomes are supported by Operate history and copied variables. The L3 form belongs to an active duplicate, while the completed L3 evidence uses a different instance. L2 rejection is supported by copied variables and the completed instance list rather than a dedicated final detail image.
