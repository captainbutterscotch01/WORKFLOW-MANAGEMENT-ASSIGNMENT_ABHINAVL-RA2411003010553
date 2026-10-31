# Lab 2 - workflow execution test matrix

Live execution was performed on a Windows/Docker installation on 30-31 October 2026. Results below were transcribed from Operate/Tasklist screenshots and copied process variables. Fifteen required scenarios have observed terminal outcomes. Seven additional example scenarios are not claimed as live-tested. Screenshot times are shown as displayed on the test PC.

| Case / input file | Expected route/result | Actual outcome / instance key | Status |
|---|---|---|---|
| `O1-paid`; `examples/O1-paid.json` | Fulfilled | Paid; Fulfilled; instance 2251799813696379 | Passed |
| `O2-no-stock`; `examples/O2-no-stock.json` | Out of stock | Out of stock; instance 2251799813702359 | Passed |
| `O3-declined`; `examples/O3-declined.json` | Payment declined | Declined; Payment declined; instance 2251799813702757 | Passed |
| `O4-high-value`; `examples/O4-high-value.json` | High-value review; Fulfilled after reviewer approval | High-value review -> approved -> fulfilled route completed; instance 2251799813704225 | Passed |
| `O5-payment-error`; `examples/O5-payment-error.json` | Payment connector incident; Fulfilled after fault cleared and retry | Payment incident -> correction/retry -> fulfilled route completed; instance 2251799813715498 | Passed |
| `O6-inventory-error`; `examples/O6-inventory-error.json` | Inventory worker incident; Fulfilled after fault cleared and retry | Not run; optional additional example; instance None | Not run (optional) |
| `O7-review-reject`; `examples/O7-review-reject.json` | Review rejected; no payment attempted | Not run; optional additional example; instance None | Not run (optional) |
| `O8-invalid`; `examples/O8-invalid.json` | Invalid order | Not run; optional additional example; instance None | Not run (optional) |

Full input values are in the referenced JSON files. Failure examples require enabling their named mock fault before start.

Incident and recovery screenshots use matching instance keys for O5. High-value Tasklist evidence is labelled by its actual content, correcting the original upload name.
