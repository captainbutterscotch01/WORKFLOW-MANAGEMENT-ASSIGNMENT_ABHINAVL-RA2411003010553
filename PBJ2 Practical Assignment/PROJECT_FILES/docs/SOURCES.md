# Implementation references

The provided Camunda8_Practical_Assignment_Labs_1_to_3.pdf is the requirement source. Implementation targets Camunda 8.8.0 with Java 17 / Spring Boot 3.5.6, rather than assuming the newest API is compatible.

- https://docs.camunda.io/docs/8.8/apis-tools/camunda-spring-boot-starter/getting-started/
- https://docs.camunda.io/docs/8.8/components/connectors/protocol/rest/
- https://docs.camunda.io/docs/8.8/components/modeler/bpmn/user-tasks/
- https://docs.camunda.io/docs/8.8/components/modeler/bpmn/business-rule-tasks/
- https://docs.camunda.io/docs/8.8/self-managed/quickstart/developer-quickstart/docker-compose/
- https://github.com/camunda/camunda-distributions/blob/main/docker-compose/versions/camunda-8.8/docker-compose.yaml
- https://github.com/camunda/connectors/blob/8.8.0/connectors/http/rest/element-templates/http-json-connector.json

The development Compose configuration is adapted from the official Camunda distribution; service versions are pinned in .env, an application service is added, and host ports are restricted to loopback. These versions are for reproducibility of the educational project, not a recommendation for production deployment. Review Camunda licensing before non-educational deployment.
