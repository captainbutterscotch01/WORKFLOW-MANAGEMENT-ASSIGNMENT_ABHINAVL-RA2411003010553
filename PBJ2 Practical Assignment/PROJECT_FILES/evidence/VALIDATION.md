# Completed verification and remaining evidence

## Executed successfully

- Java 17 / Maven build and executable Spring Boot JAR packaging: PASS.
- JUnit: 39 tests, 0 failures, 0 errors, 0 skipped. See junit/ and maven-build.log.
- Tests include 19 DMN evaluations (loan thresholds, all order priority conditions, all boolean triage combinations), field validation, catalog calculations, fault clearing, HTTP contracts, and payment/booking idempotency.
- All three BPMN files parsed and validated using the Camunda 8.8 Zeebe BPMN model library and the generic Camunda BPMN model library.
- DMN evaluations were executed using Camunda 7.24's standalone DMN engine with FEEL, NOT the deployed Camunda 8 engine. The rules use common FEEL constructs, but this does not establish Camunda 8 deployment compatibility by itself.
- Structural checks verified flow references, reachability, gateway default/condition coverage, decision/form deployment bindings and unique form keys. See model-structure-checks.txt.
- The packaged Spring Boot application was actually started in mock-only mode. 20 real HTTP responses were checked over localhost, including success, decline, no slots, idempotency and HTTP 503 followed by correction for all three mock services. See http-smoke-results.json. This was NOT a REST Connector/Zeebe execution.
- PDF reports were rendered and inspected. Diagrams were generated from the real BPMN DI/XML, not from live Modeler, Tasklist or Operate screenshots.

## Live runtime evidence supplied by the student

Live execution was performed on the student's Windows/Docker installation on 30-31 October 2026. Results below were transcribed from supplied Operate/Tasklist screenshots and copied process variables; the report-generation environment did not connect to the cluster. Fifteen required scenarios have observed terminal outcomes. Seven additional example scenarios are not claimed as live-tested. Screenshot times are as displayed on the student PC.

H5: the incident screenshot is a Lab 3 dashboard view without an instance key. The recovered H5 instance is 2251799813723383. Their association follows the supplied test sequence and user-reported correction/retry, not an independently visible before/after key match. No detailed incident screenshot was supplied. Tasklist captures include pre-completion forms/lists; final human outcomes are supported by Operate history and copied variables. The L3 form belongs to an active duplicate, while the completed L3 evidence uses a different instance. L2 rejection is supported by copied variables and the completed instance list rather than a dedicated final detail image.

See runtime/INDEX.md, runtime/results.json and the three updated test matrices. The earlier JUnit/build logs are retained. On 31 October 2026, the included JAR was started in mock-only mode and all 20 HTTP checks passed again; model structural checks also passed. A fresh Maven rebuild was attempted but dependency resolution was blocked by network/DNS access. No new end-to-end Camunda run was performed during packaging. Source, models, configuration, scripts and the JAR match the previously executed project byte for byte.
## Live runtime evidence supplied by the student

Live execution was performed on a Windows/Docker installation on 30-31 October 2026. Results below were transcribed from Operate/Tasklist screenshots and copied process variables. Fifteen required scenarios have observed terminal outcomes. Seven additional example scenarios are not claimed as live-tested. Screenshot times are shown as displayed on the test PC.

H5: the incident screenshot is a Lab 3 dashboard view without an instance key. The recovered H5 instance is 2251799813723383. Their association follows the supplied test sequence and user-reported correction/retry, not an independently visible before/after key match. No detailed incident screenshot was supplied. Tasklist captures include pre-completion forms/lists; final human outcomes are supported by Operate history and copied variables. The L3 form belongs to an active duplicate, while the completed L3 evidence uses a different instance. L2 rejection is supported by copied variables and the completed instance list rather than a dedicated final detail image.

See runtime/INDEX.md, runtime/results.json and the three updated test matrices. The earlier JUnit/build logs are retained. On 31 October 2026, the included JAR was started in mock-only mode and all 20 HTTP checks passed again; model structural checks also passed. A fresh Maven rebuild was attempted but dependency resolution was blocked by network/DNS access. No new end-to-end Camunda run was performed during packaging. Source, models, configuration, scripts and the JAR match the previously executed project byte for byte.
