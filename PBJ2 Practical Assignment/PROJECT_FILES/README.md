# WORKFLOW MANAGEMENT - Camunda 8 Labs 1 to 3

**Name:** ABHINAV L  
**Register number:** RA2411003010553

Three independent executable BPMN processes, three DMN tables, five Camunda Forms, a Java 17/Spring Boot application, mock REST services, test inputs, and reports.

## Test results

This package includes the implementation, local test logs, and runtime evidence for the 15 required scenarios. See `evidence/runtime/INDEX.md` and the updated reports/test matrices. H5 incident evidence is dashboard-level without an instance key; the report states this limitation. Seven optional examples are not claimed as live-tested.

## Windows quick start

1. Install Docker Desktop with Linux containers and Docker Compose v2. Give Docker approximately 8 GB RAM. Start Docker Desktop. Internet is needed for the initial image/dependency downloads.
2. Extract the ZIP. Open PowerShell in the folder containing `compose.yaml` and `pom.xml`.
3. Run `docker compose up --build -d`. The first build runs the tests. Wait for services to start. Check `docker compose ps` and `docker compose logs --tail=100 labs orchestration connectors` if anything exits.
4. Open `http://localhost:8090/mock/health`. It should return `{"status":"UP"}`.
5. Open `http://localhost:8088/operate` and `http://localhost:8088/tasklist`. The development account is `demo` / `demo`. These are public local development defaults, not real credentials.
6. Deploy the models using `curl.exe -X POST http://localhost:8090/api/deploy`.
7. Start the required examples individually. For example, in CMD: `curl.exe -X POST http://localhost:8090/api/start/loan -H "Content-Type: application/json" --data-binary "@examples/L1-approved.json"`. Use `/api/start/order` or `/api/start/hospital` for the other labs. Complete required human tasks in Tasklist. See the recovery steps below for fault tests. The supplied `scripts/run-cases.ps1` is an optional batch runner that starts all 22 examples, including optional cases; it is not needed to inspect this submission.
8. Run `docker compose stop` when finished. `docker compose down` removes containers but preserves named data volumes. Do not delete volumes before collecting evidence.

All exposed ports are bound to localhost. This is a local educational stack with an unprotected API; do not expose it to a network. Do not change the mock tokens to real payment data.

## Opening models

Use Camunda Desktop Modeler supporting Camunda 8.8. Open `src/main/resources/models/*.bpmn` and `*.dmn`. The `.form` files are native Camunda Forms. The engine's REST connector configuration is embedded in the BPMN XML; no proprietary template is required to execute it. Generic Modeler panels may show its input mappings and headers instead of a branded connector panel. To use the branded panel, install/apply the Camunda 8.8 REST template and preserve the supplied mappings.

`docs/diagrams/` contains renders of the actual BPMN files, NOT proof of deployment. Deployment binding keeps each BPMN version linked to the DMN/form version deployed alongside it.

## Alternative: Java on the host

Requires Java 17 and Maven 3.9+. First run `docker compose up -d orchestration connectors elasticsearch`. Then set `$env:CONNECTOR_BASE_URL="http://host.docker.internal:8090"` and run `mvn spring-boot:run`. Do not also run the `labs` container. On Linux, the connector container needs a host-gateway mapping for host.docker.internal; the default all-Docker setup avoids this issue.

A prebuilt executable JAR is included in `bin/`. With the Camunda services running and the correct CONNECTOR_BASE_URL set, use `java -jar bin/camunda-labs-1.0.0.jar` instead of Maven.

`mvn clean verify` runs validation, DMN and mock API tests. `mvn package` creates `target/camunda-labs-1.0.0.jar`. Python users can run `python scripts/run_cases.py` instead of PowerShell.

## Folder map

- `src/main/resources/models`: BPMN, DMN and linked forms, the authoritative model files.
- `src/main/java/edu/srm/camundalabs`: Spring Boot workers, deterministic validation, mock services, deployment/start API.
- `src/test/java`: executable unit, DMN and HTTP-contract checks.
- `examples`: one JSON per test case, scenario manifest and importable Postman collection.
- `docs`: individual implementation reports, dictionaries, rule descriptions, test matrices, API documentation and diagrams.
- `evidence`: local validation output and runtime screenshots; `live` is created by the optional batch runner.

## Deliberate scope

Loan amounts/prices are synthetic INR values. Hospital rules are predefined boolean flags solely for a classroom simulation, not a clinical triage system. Inventory is a fixed availability snapshot; stock is not reserved or deducted. Fulfilment records a simulated outcome, not a shipment. Booking and payment responses are idempotent by caseId only while the mock application stays running. Mocks are in memory and reset on restart. Use a fresh caseId for each independent test; retries of the SAME case reuse its caseId. No real patient data, payment details or external secrets are used.

## Failure and recovery tests

Use CMD in this project folder. Enable the fault for the chosen case before starting its JSON file:

```cmd
curl.exe -X POST http://localhost:8090/mock/faults/scheduling/H5-scheduling-error -H "Content-Type: application/json" -d "{\"enabled\":true}"
curl.exe -X POST http://localhost:8090/api/start/hospital -H "Content-Type: application/json" --data-binary "@examples/H5-scheduling-error.json"
```

Inspect the failed instance in Operate and capture the error. Clear the fault:

```cmd
curl.exe -X POST http://localhost:8090/mock/faults/scheduling/H5-scheduling-error -H "Content-Type: application/json" -d "{\"enabled\":false}"
```

Retry the failed job in the same Operate instance, then check its end date and outcome. The other required fault/case pairs are `credit/L4-credit-error`, `loan-worker/L5-worker-error` and `payment/O5-payment-error`. Start those cases using the loan or order endpoint respectively.

## Reports and evidence

The three PDF reports are in `docs/`. Each report includes its flow, decision rules, example evaluations, test results, assumptions and screenshots. Full inputs are in `examples/`; test tables are in `docs/LAB1_TEST_CASES.md` through `LAB3_TEST_CASES.md`. Full-resolution screenshots and their descriptions are in `evidence/runtime/`.

Keep `.env` beside `compose.yaml`; Docker Compose requires its version settings. The `bin/` JAR supports the Java startup option. Docker builds directly from the source.
