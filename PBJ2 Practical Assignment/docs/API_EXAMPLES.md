# REST and process APIs

All requests use `Content-Type: application/json`. Base URL from host: `http://localhost:8090`. The connector uses `http://labs:8090` inside Docker. Authentication is none for these synthetic services.

## Deployment and start

POST `/api/deploy` deploys every BPMN, DMN and form in one request. Response contains a real deploymentKey and processCount=3. POST `/api/start/loan`, `/api/start/order`, or `/api/start/hospital` accepts one example JSON and returns a real processInstanceKey. The start endpoint adds `reviewer=demo` if absent and the configured `mockBaseUrl`.

## Credit

POST `/mock/credit`
Request: `{"caseId":"L-demo","applicantRef":"SYN-A001","creditProfile":"good"}`
Response: `{"score":780,"assessmentStatus":"OK"}`.
`borderline` gives 650, `poor` gives 500. An unknown profile is HTTP 400. The connector output mapping is `={creditScore:response.body.score, creditStatus:response.body.assessmentStatus}`.

## Payment

POST `/mock/payment`
Request: `{"caseId":"O-demo","orderTotal":1000,"paymentToken":"tok_success"}`
Response: `{"paymentStatus":"Paid","transactionRef":"SYN-PAY-O-demo"}`.
Use tok_decline to return Declined. Unknown synthetic tokens also decline. Requests must have a positive total and nonempty caseId. The connector output mapping is `={paymentStatus:response.body.paymentStatus}`. The same caseId reuses the prior result to avoid duplicate payment simulation during retries.

## Scheduling and booking

POST `/mock/scheduling`
Request: `{"caseId":"H-demo","patientRef":"SYN-P001","appointmentPreference":"Morning","slotScenario":"available"}`
Response: `{"availableSlots":["SYN-MORNING-001","SYN-MORNING-002"],"slotsAvailable":true}`.
`slotScenario=none` returns an empty list and false. Afternoon returns SYN-AFTERNOON identifiers. These are synthetic identifiers, not real dated appointments.

The Java availability worker calls the API first and stores preliminaryAvailability. The REST connector then obtains the current slot list with `={availableSlots:response.body.availableSlots,slotsAvailable:response.body.slotsAvailable}`. The two steps deliberately satisfy both the worker and connector integration requirements. Urgent cases bypass both steps.

POST `/mock/bookings`
Request: `{"caseId":"H-demo","selectedSlot":"SYN-MORNING-001"}`
Response: `{"bookingStatus":"Booked","bookingReference":"SYN-BOOK-H-demo","selectedSlot":"SYN-MORNING-001"}`.
The booking worker uses the first returned slot, with the same caseId as an idempotency key. Slot IDs are fixed mock identifiers and are not exclusive reservations across patients.

## Failure controls

POST `/mock/faults/credit/L-demo` with `{"enabled":true}` causes subsequent credit requests for that case to return HTTP 503. Send `{"enabled":false}` to restore the service. Other supported service names: payment, scheduling, loan-worker, inventory. Worker faults create a failed Camunda job with zero retries. HTTP status errors are connector failures, not business declines.

## Connector details shared by all three models

- Job type: `io.camunda:http-json:1`; retries: 1.
- Authentication input `authentication.type`: `noAuth`.
- HTTP method: POST. Header expression: `={"Content-Type":"application/json"}`.
- URL expression: `=mockBaseUrl + "/mock/credit"` (or /payment, /scheduling).
- Connection timeout: 5 seconds. Read timeout: 10 seconds.
- Request body: explicit FEEL context built from the workflow variables.
- Result expression: task header `resultExpression`, mapping `response.body` fields to named workflow variables.
- Do not name global process variables body, url, method, headers, authentication or other reserved connector inputs.

Full executable requests are included in the Postman collection and per-case JSON examples. Example responses above document the contract; live workflow proof must come from your run.
