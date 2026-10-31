# Process-variable dictionary

## Shared

| Variable | Type | Writer | Purpose |
|---|---|---|---|
| caseId | string | starter | Unique synthetic case; stable idempotency/fault key across retries |
| reviewer | string | starter; default demo | Exact Tasklist assignee username |
| mockBaseUrl | string | start API configuration | Connector-visible API host; default http://labs:8090 |
| validationErrors | list of strings | validation worker | Field-level validation failures |
| reviewDecision | string: Approve/Reject | review form | Explicit decision; used in gateway FEEL |
| reviewNotes | string | staff form | Required synthetic rationale/action |
| staffActionRecorded | boolean | urgent/no-slot form | Required acknowledgement |
| finalStatus | string, task-local input | record-outcome input mapping | Requested terminal status |
| outcome | string | record-outcome worker | Final business outcome persisted at process scope |

## Lab 1

| Variable | Type | Purpose |
|---|---|---|
| applicantRef, applicantName | string | Synthetic applicant identifiers |
| age | integer | Age in years; positive validation, eligibility >=21 |
| loanAmount | number | Positive requested amount in synthetic INR |
| monthlyIncome | number | Positive monthly income in synthetic INR |
| creditProfile | good / borderline / poor | Deterministic mock scenario |
| validationPassed | boolean | Applicant structural validation result |
| creditScore | number | Credit connector output: 780 / 650 / 500 |
| creditStatus | string | Successful API assessment status, OK |
| loanDecision | Approved / Rejected / Manual Review | DMN FIRST output |

## Lab 2

| Variable | Type | Purpose |
|---|---|---|
| customerRef | string | Synthetic customer identifier |
| productId | P100 / P200 / P300 | Fixed catalog item |
| quantity | integer 1..1000 | Requested units |
| customerCategory | Standard / VIP | Priority input |
| deliveryPreference | Standard / Express | Priority input |
| paymentToken | string | tok_success or tok_decline; unknown tokens decline |
| orderValid | boolean | Field/catalog validation result |
| stockAvailable | boolean | Fixed stock >= quantity for valid order |
| orderTotal | decimal number | Catalog unit price multiplied by quantity |
| deliveryPriority | High / Medium / Normal | DMN FIRST output |
| paymentStatus | Paid / Declined | Mock REST response; HTTP errors cause incidents |

Catalog: P100 = INR 500, stock 20; P200 = INR 30000, stock 10; P300 = INR 1500, stock 0. Stock is a fixed simulated snapshot.

## Lab 3

| Variable | Type | Purpose |
|---|---|---|
| patientRef | string | Synthetic identifier only |
| appointmentPreference | Morning / Afternoon | Requested mock session |
| symptomsSummary | string | Fictional narrative; not used to infer clinical urgency |
| urgentIndicator | boolean | Predefined educational urgent flag |
| priorityIndicator | boolean | Predefined educational priority flag |
| slotScenario | available / none | Synthetic capacity scenario |
| registrationValid | boolean | Field/type validation result |
| urgencyCategory | Routine / Priority Review / Urgent Assessment | DMN result with urgent precedence |
| preliminaryAvailability | object | Worker scheduling API response |
| availableSlots | list of strings | Fresh connector slot IDs |
| slotsAvailable | boolean | Fresh connector availability flag |
| selectedSlot | string | First available synthetic slot |
| bookingReference | string | Idempotent mock booking reference |
| bookingStatus | string | Booked only after successful booking API response |
