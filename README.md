# Spring Boot Microservice Template

Spring Boot 3 based microservice template integrating features which address a majority of common business requirements.

Contact: Alex Stone (alex.stone@cgi.com)

# Table of Contents

1. [Introduction and Purpose](#introduction-and-purpose)
2. [Run the Pet Store Microservice](#run-the-pet-store-microservice)
3. [Run Unit and Integration Tests](#run-unit-and-integration-tests)
4. [Dependency Version Management](#dependency-version-management)
5. [OpenAPI/Swagger Code Generation](#openapiswagger-code-generation)
6. [Data Persistence via MongoDB](#data-persistence-via-mongodb)
7. [Exception Handling](#exception-handling)
8. [Request Validation via OpenAPI Specification](#request-validation-via-openapi-specification)
9. [Java Request Validation](#java-request-validation)
10. [Mapping between Pet Store API model, external API model and MongoDB Documents](#mapping-between-pet-store-api-model-external-api-model-and-mongodb-documents)
11. [Unit Tests](#unit-tests)
12. [Integration Tests](#integration-tests)
13. [Dynamic port allocation and discovery for local development](#dynamic-port-allocation-and-discovery-for-local-development)
14. [OAuth2 Security Integration](#oauth2-security-integration)
15. [Metrics Endpoint](#metrics-endpoint)
16. [External REST API Call with Retry via Spring WebFlux](#external-rest-api-call-with-retry-via-spring-webflux)
17. [Stubbing of External API Calls via WireMock](#stubbing-of-external-api-calls-via-wire-mock)
18. [Logging of Requests and Responses](#logging-of-requests-and-responses)
19. [Logging with a Mapped Diagnostic Context (MDC)](#logging-with-a-mapped-diagnostic-context-mdc)
20. [Structured JSON logging](#structured-json-logging)
21. [Unit testing of Logging](#unit-testing-of-logging)
22. [Execute Postman API test Collection as a Gradle task](#execute-postman-api-test-collection-as-a-gradle-task)
23. [Load Testing](#load-testing)
24. [Actuator Endpoints](#actuator-endpoints)
25. [Swagger Documentation Endpoints](#swagger-documentation-endpoints)
26. [Automated Code Style Formatting](#automated-code-style-formatting)
27. [Check for updates to dependencies](#check-for-updates-to-dependencies)
28. [Architecture Unit Tests](#architecture-unit-tests)
29. [Verifiable Git commit id](#verifiable-git-commit-id)
30. [Notes](#notes)

---

#### Introduction and Purpose

This template is designed to serve as a go-to reference for implementing many Spring Boot microservice features.
It is _representative_ of a real-life production ready microservice, although clearly depending on your specific
requirements you will likely have to make some modifications to the approaches included in this project.

At a minimum, the template should provide a high quality default starting point for new features.

The API is defined by the OpenAPI specification [pet-store-api.yaml](src%2Fmain%2Fresources%2Fopenapi%2Fpet-store-api.yaml) and can be viewed in the Swagger
Editor https://editor.swagger.io/

The microservice is structured with Controller and Service layers.
Depending on the use case it may be desirable to also include a mapping layer to translate between one or more of the
following:

- API model types (pet store)
- External API call - API model types (vaccinations)
- Datastore/Repository entity types (MongoDB)

The API provides the following functionality:

- New pets can be added to the pet store.
- Customers can search for a pet by either Pet Id, or PetAvailabilityStatus.
- Pets can be updated when details change.
- Customers can purchase a pet.
- Pet vaccination details are provided via an external API call.
- Pets can be removed (archived) from the pet store.

---

#### Run the Pet Store microservice
Requirements to run locally:

* Java 22 e.g. https://jdk.java.net/22/
* An IDE, IntelliJ (recommended), or Eclipse
* (Nice to have) MongoDB or equivalent (e.g. Microsoft Azure CosmosDB via [CGI
  Sandbox](https://ensemble.ent.cgi.com/business/305832/serviceexcellence/Service%20Excellence%20Wiki%20Library/Sandbox.aspx))
* Node.js and Newman, see [Installing Node.js and Newman](#installing-nodejs-and-newman)

When the above requirements have been satisfied, to start the Pet Store Microservice:

1. Start the Wire Mock stub server:  
   `./gradlew startWireMockEmbedded`
2. Start the in-memory MongoDB server:  
   `./gradlew startMongoDBEmbedded`
3. Start the in-memory OAuth2 server:  
   `./gradlew startOAuth2Embedded`
4. Start the microservice:  
   `./gradlew bootRun --args='--spring.profiles.active=local'`
5. Execute the API tests:  
   `./gradlew :api-test:run`
6. Execute the load tests:  
   `./gradlew :load-test:run`

Alternatively, you can execute all of the above by opening a terminal (IntelliJ) or command prompt (cmd) and executing
the script:  
WIP Alex `.\cleanBuildTestAndRunLocally.ps1` WIP Alex

To explore the microservice endpoints, you can view the Swagger UI
at [http://localhost:{applicationPort}/swagger-ui/index.html](http://localhost:{applicationPort}/swagger-ui/index.html)
where the `{applicationPort}` can be found
in [dynamicApplicationProperties.json](common%2Fbuild%2Ftmp%2Flocal%2FdynamicApplicationProperties.json).

You can also open the Postman collection  
[spring-boot-microservice-template.postman_collection.json](api-test%2Fsrc%2Fmain%2Fresources%2Fspring-boot-microservice-template.postman_collection.json)
and [local.postman_environment.json](api-test%2Fsrc%2Fmain%2Fresources%2Flocal.postman_environment.json)
setting your environment variables (port numbers)
using [dynamicApplicationProperties.json](common%2Fbuild%2Ftmp%2Flocal%2FdynamicApplicationProperties.json)

---

#### Run Unit and Integration tests

The integration tests are identified by the JUnit annotation `@Tag("integration")` which is also present in
[BaseIntegrationTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2FBaseIntegrationTest.java)
which is typically extended by integration tests.  
While unit tests are identified by the JUnit annotation `@Tag("unit")`.

By default, all tests (unit and integration) are run:  
`./gradlew test`

---

#### Dependency Version Management

To best manage a large number of Spring dependencies with independent version numbers this template uses the
following Spring Gradle plugins:

- `org.springframework.boot`
- `io.spring.dependency-management`

In addition to the dependency management BOMs (Bill Of Materials):

- `org.springframework.boot:spring-boot-dependencies`
- `org.junit:junit-bom`

With all Gradle dependency and plugin versions defined in one place [gradle.properties](gradle.properties).

---

#### OpenAPI/Swagger code generation

See the `build.gradle` for an example of using OpenAPI schemas (`pet-store-api.yaml` and `animal-vaccination-api.yaml`)
to generate model classes and the Java interfaces for the APIs.  
By having a controller implement the Java interface derived from the API, when the schema is updated some
breaking changes will be caught as compile time errors.

To generate all OpenAPI Java classes and interfaces:  
`./gradlew generateAllOpenAPI`

To generate the Pet Store API classes and interfaces:  
`./gradlew generatePetStoreClasses`

To generate the external Animal Vaccination API classes and interfaces:  
`./gradlew generateExternalAnimalVaccinationClasses`

---

#### Data persistence via MongoDB

This service is integrated with a MongoDB NoSQL database using `spring-boot-starter-data-mongodb`
and the `MongoRepository` interface. Connection details are defined in the `application.yaml` as a connection string
using the property `spring.data.mongodb.uri`.

For a more lightweight and simpler MongoDB integration consider using
the [mongodb-driver-sync](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync/4.11.1)
client library. Although the out-of-the-box features of `MongoRepository` and `@Document` will not be available.

---

#### Exception Handling

Any exceptions which are thrown by the microservice will be caught and handled by
the [GlobalExceptionHandler.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fexception%2Fhandler%2FGlobalExceptionHandler.java).
All application exceptions
extend [ApplicationException.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fexception%2FApplicationException.java)
which allows you to specify both a message and the HTTP status code which should be used in the response.

---

#### Request validation via OpenAPI specification

See the `/pets/{petId}` GET endpoint and the `PetId` schema definition
in [pet-store-api.yaml](src%2Fmain%2Fresources%2Fopenapi%2Fpet-store-api.yaml)
and the associated integration test `should_ReturnError_When_CallingGetPetEndpointWithIdLargerThanPermitted`
in [ApplicationIntegrationTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2FApplicationIntegrationTest.java)
for details of how request validation can be implemented via the Open API yaml.

---

#### Java Request Validation

See [PetIdValidator.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fcontroller%2Fvalidation%2FPetIdValidator.java) and the integration test
`should_ReturnError_When_CallingGetPetEndpointWithInvalidIdFailingValidation`  
for details of how to write custom request validation logic.

Wherever possible, request validation logic should be defined in the OpenAPI definition e.g. [pet-store-api.yaml](src%2Fmain%2Fresources%2Fopenapi%2Fpet-store-api.yaml)  
This has several benefits over custom Java validators, including:
- Reusable and language agnostic, such that you can use the OpenAPI yaml to rewrite the microservice using another (non-Java) implementation language.
- The validation is visible by all consumers of the microservice via the OpenAPI yaml.
- The API documentation will always be up-to-date with the validation implementation.

---

#### Mapping between Pet Store API model, external API model and MongoDB Documents

See the below classes for examples of different mappers:
- [PetMapper.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fservice%2Fpet%2FPetMapper.java)
- [ExternalVaccinationsMapper.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fexternal%2Fvaccinations%2FExternalVaccinationsMapper.java)
- [CustomerMapper.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fservice%2Fcustomer%2FCustomerMapper.java)

Depending on the use case [MapStruct](https://mapstruct.org/) is also an option for reducing boilerplate mapping code.
This should to be weighed up against the increase in complexity of the mapping implementation.

---

#### Unit Tests

All JUnit based unit tests have the annotation `@Tag("unit")` so they can be easily identified and executed
independently of slower running integration tests.

To run only unit tests:  
`./gradlew -PincludeTag=unit test`  
or  
`./gradlew -PexcludeTag=integration test`

---

#### Integration Tests

All JUnit based integration tests have the annotation `@Tag("integration")` so they can be easily identified and
executed when required.

Typically, integration tests extend the base
class [BaseIntegrationTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2FBaseIntegrationTest.java)
and use Wire
Mock [WireMockForIntegrationTests.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2Futils%2FWireMockForIntegrationTests.java)
and `de.flapdoodle.embed.mongo` [MongoDbForIntegrationTests.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2Futils%2FMongoDbForIntegrationTests.java)
to stand-in for external dependencies.

To run only integration tests:  
`./gradlew -PincludeTag=integration test`  
or  
`./gradlew -PexcludeTag=unit test`

---

#### Dynamic port allocation and discovery for local development

To improve developer efficiency, when running the microservice and associated dependencies like Wire Mock, MongoDB
and OAuth2 locally, all port numbers are assigned dynamically and subsequently discovered when needed via the
[DynamicApplicationPropertiesRepository.java](common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fcommon%2Flocal%2FDynamicApplicationPropertiesRepository.java).

For example, when you start
[WireMockEmbedded.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Futils%2Fembedded%2FWireMockEmbedded.java)
the
dynamic port number (determined at runtime) which
it is listening on is persisted
to [dynamicApplicationProperties.json](common%2Fbuild%2Ftmp%2Flocal%2FdynamicApplicationProperties.json)
such that when you start the microservice (with the `local` profile) it will be automatically configured to use the
correct
Wire Mock port.

Port allocation and discovery includes:

- Embedded Wire Mock `./gradlew startWireMockEmbedded`
- Embedded MongoDB `./gradlew startMongoDBEmbedded`
- Embedded OAuth2 server: `./gradlew startOAuth2Embedded`
- Pet Store microservice `./gradlew bootRun --args='--spring.profiles.active=local'`
- API Tests `./gradlew :api-test:run`
- Load Tests `./gradlew :load-test:run`

---

#### OAuth2 Security Integration

See `spring.security.oauth2` in [application.yaml](src%2Fmain%2Fresources%2Fapplication.yaml) for the required
application configuration changes.

Also
see [SecurityConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FSecurityConfiguration.java)
for details of how to configure a `SecurityFilterChain` to allow both unauthenticated endpoints like Swagger,
and OAuth2 authenticated application API endpoints.

Testing is facilitated with `no.nav.security:mock-oauth2-server` (see [build.gradle](build.gradle)) and the embedded
OAuth2
server [OAuth2Embedded.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Futils%2Fembedded%2FOAuth2Embedded.java).

**Note**: That Spring OAuth2 configuration properties (e.g. `spring.security.oauth2resource-server.jwt.issuer-uri`)
are resolved very early in the creation of the application context.  
To ensure that the system property `OAUTH_HOST` is overridden before the application context is created we
define [OAuth2Embedded.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Futils%2Fembedded%2FOAuth2Embedded.java)
as
a `private static final` variable
in [BaseIntegrationTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2FBaseIntegrationTest.java).
So the OAuth2 server is initialised and system properties are set before Spring Security initialises OAuth2.

---

#### Metrics Endpoint

See the metrics endpoint provided by Spring Actuator https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints
- GET http://localhost:8099/actuator/metrics

---

#### External REST API call with retry via Spring WebFlux

See [VaccinationsApiClient.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fexternal%2Fvaccinations%2FVaccinationsApiClient.java)
for an example of making an external REST API call with retry logic using the SpringFlux `WebClient`.

---

#### Stubbing of external API calls via Wire Mock

See https://wiremock.org/docs/stubbing/ for additional guidance with Wire Mock.

Also
see [WireMockEmbedded.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Futils%2Fembedded%2FWireMockEmbedded.java)
for how to run a stand-alone embedded stub server for running a microservice
locally which has external API dependencies.

Along with the Wire Mock mapping and stub response files in [wiremock](src%2Ftest%2Fresources%2Fwiremock) directory.

---

#### Logging of requests and responses

See [RequestLoggingFilterConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2FRequestLoggingFilterConfiguration.java)
for the required config to log requests using the `org.springframework.web.filter.CommonsRequestLoggingFilter`.

Also consider the use of
AOP [LoggingAspects.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2Faspects%2FLoggingAspects.java)
and the `@LogMethodArguments` annotations to log method arguments,
and the `@LogMethodResponse` to log the method return object.

---

#### Logging with a Mapped Diagnostic Context (MDC)

To improve the traceability of user actions when looking at the microservice logs there is a Mapped Diagnostic Context (MDC).
All MDC keys are defined in 1 central
place [MappedDiagnosticContextKey.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2Fmdc%2FMappedDiagnosticContextKey.java),
along with the logic to clear the MDC.  
Calling `MDC.remove()` for only the required keys has a very slight performance improvement over calling `MDC.clear()`.

The
class [ClearMappedDiagnosticContextWhenDone.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2Fmdc%2FClearMappedDiagnosticContextWhenDone.java)
ensures that the MDC for the thread is cleared down after each request.

The
class [AddUniqueRequestIdToMappedDiagnosticContextAndResponse.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2Fmdc%2FAddUniqueRequestIdToMappedDiagnosticContextAndResponse.java)
and the associated `addRequestIdToLoggingFilter` configuration
in [WebConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FWebConfiguration.java)
adds a unique `requestId` (UUID) to the MDC for each request and includes the same id as a header in the response.

The
class [AddUsernameToMappedDiagnosticContext.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2Fmdc%2FAddUsernameToMappedDiagnosticContext.java)
and the associated `addInterceptors` configuration
in [WebConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FWebConfiguration.java)
includes the `username`, which is derived from the `HttpServletRequest Principal`.

---

#### Structured JSON Logging

To improve the search-ability of application logs they are structured using JSON to provide a balance between both
machine and developer readability.

This is implemented using the dependency `net.logstash.logback:logstash-logback-encoder` and the appropriate config in
[logback.xml](src%2Fmain%2Fresources%2Flogback.xml).

---

#### Unit testing of Logging

The JUnit extension
class [LoggingVerification.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Futils%2FLoggingVerification.java)
can be used to conveniently unit test logging.
This is done by including the following annotations on the test class:

`@ExtendWith(LoggingVerification.class)`  
`@TestLoggingTarget(MappedDiagnosticContextKey.class)`

And verifying log statements using the `LoggingVerification` class like below:  
`LoggingVerification.assertLog(Level.DEBUG, Matchers.equalTo("Clearing all MDC keys"));`

For an example
see [MappedDiagnosticContextKeyTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Flogging%2Fmdc%2FMappedDiagnosticContextKeyTest.java).

---

#### Execute Postman API test Collection as a Gradle task

The API tests consist of
a [Postman Collection](api-test%2Fsrc%2Fmain%2Fresources%2Fspring-boot-microservice-template.postman_collection.json)
and are run using [Newman](https://github.com/postmanlabs/newman).  
They are executed
via [ApiTestApplication.java](api-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fapitest%2FApiTestApplication.java)
which obtains the required port numbers
from [DynamicApplicationPropertiesRepository.java](common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fcommon%2Flocal%2FDynamicApplicationPropertiesRepository.java)
to execute [Newman](https://github.com/postmanlabs/newman) with the required command line arguments.

To execute the Postman Collection execute the gradle task:  
`./gradlew :api-test:run`

---

#### Load Testing

Load testing is implemented using `Gatling` for which the dependencies are defined in [load-test/build.gradle](load-test%2Fbuild.gradle).
It is executed via the [LoadTestApplication.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2FLoadTestApplication.java) class.  
The load test scenarios are defined in [LoadSimulationDefinition.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2FLoadSimulationDefinition.java).

There is also a metrics collection feature, which polls the JVM memory usage using the actuator metrics endpoint.
The metrics are recorded and reported on following the load test. See [MemoryUsageMetrics.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2Fmemory%2FMemoryUsageMetrics.java)
for implementation details.

All required port numbers are configured dynamically
in [HttpProtocolBuilders.java](load-test%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Floadtest%2Futil%2FHttpProtocolBuilders.java)
using
the [DynamicApplicationPropertiesRepository.java](common%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fcommon%2Flocal%2FDynamicApplicationPropertiesRepository.java).

See [docs.gatling.io](https://docs.gatling.io/reference/script/core/simulation/) for additional information about
`Gatling`.

---

#### Actuator Endpoints

Determined by the "OpenAPI/Swagger docs" dependencies in the [build.gradle](build.gradle),  
application config [application.yaml](src%2Fmain%2Fresources%2Fapplication.yaml)  
and security config [SecurityConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FSecurityConfiguration.java).

https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints

- GET http://localhost:8099/actuator
- GET http://localhost:8099/actuator/prometheus
- GET http://localhost:8099/actuator/health
- GET http://localhost:8099/actuator/info
- GET http://localhost:8099/actuator/metrics
- GET http://localhost:8099/actuator/mappings

Where `8099` is the Management Server Port `management.server.port`
If running with the `local` profile all dynamic port numbers are recorded
in [dynamicApplicationProperties.json](common%2Fbuild%2Ftmp%2Flocal%2FdynamicApplicationProperties.json).

---

#### Swagger Documentation Endpoints

With the [OpenApiConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fconfig%2FOpenApiConfiguration.java)
endpoints are provided to provide live up-to-date API documentation.

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs/swagger-config
- http://localhost:8080/v3/api-docs
- http://localhost:8080/v3/api-docs.yaml
- http://localhost:8080/v3/api-docs/springdoc

Where `8080` is the Application Server Port `server.port`.  
If running with the `local` profile all dynamic port numbers are recorded
in [dynamicApplicationProperties.json](common%2Fbuild%2Ftmp%2Flocal%2FdynamicApplicationProperties.json).

---

#### Automated Code Style Formatting

Ensure all Java files are formatted consistently to the Google Java Coding Standards and apply the
formatting with every build via the [Diff Plug - Spotless](https://github.com/diffplug/spotless) Gradle plugin.

Code formatting can be applied to the project via:  
`./gradlew spotlessApply`

And verified via:  
`./gradlew spotlessCheck`

See `spotless` in [build.gradle](build.gradle) for details.

---

#### Check for updates to dependencies

To check all dependencies for any updated versions use the `com.github.ben-manes.versions`
plugin https://github.com/ben-manes/gradle-versions-plugin.  
To execute it run the Gradle task and verify the output.  
`./gradlew dependencyUpdates`

You will see something like:

`The following dependencies have later milestone versions:`  
`com.fasterxml.jackson.core:jackson-databind [2.17.0 -> 2.17.1]`  
`no.nav.security:mock-oauth2-server [2.0.0 -> 2.1.5]`

It is configured via `dependencyUpdates` in [build.gradle](build.gradle) to ignore non-final release candidates
e.g. `ALPHA` and `BETA` releases.

---

#### Architecture Unit Tests

There are also automated unit tests to verify various aspects of the software architecture.  
e.g. All test classes which are not annotation with `@Disabled` should have class names ending in `Test`.

This has been implemented using `com.tngtech.archunit:archunit-junit5` and examples can be seen
in [ApplicationArchitectureTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2FApplicationArchitectureTest.java)
and [TestArchitectureTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2FTestArchitectureTest.java).

---

#### Verifiable Git commit id

It is often useful to be able to verify which version of a service is running on a given host/environment.
By utilising the `com.gorylenko.gradle-git-properties` plugin the Git commit id, branch and build tags are
included in the Spring `/info` actuator endpoint.

The Git attributes included are determined by the `gitProperties` attribute in the [build.gradle](build.gradle)
and the `management.info.git.mode: full` application configuration in
the [application.yaml](src%2Fmain%2Fresources%2Fapplication.yaml).

This is tested by `actuatorInfoEndpointShouldIncludeArtifactAndGitDetails`
in [ActuatorAndDocsIntegrationTest.java](src%2Ftest%2Fjava%2Fcom%2Fcgi%2Fexample%2Fpetstore%2Fintegration%2FActuatorAndDocsIntegrationTest.java).

e.g.

```json
{
   "git": {
      "branch": "master",
      "tags": "0.9.0",
      "commit": {
         "id": "fcdd60b6c2fa16fb4b3fe31e39084815a5284692"
      },
      "build": {
         "version": "1.0.0-SNAPSHOT"
      },
      "remote": {
         "origin": {
            "url": "https://pauksource.ent.cgi.com/gitlab/uka/SBUDCDADA/dsc-ds-development-best-practice/spring-boot-microservice-template.git"
         }
      }
   }
}
```

---

### Notes

#### Installing Node.js and Newman

Install `Node.js` with a version greater than 16 as
per https://github.com/postmanlabs/newman?tab=readme-ov-file#getting-started.  
On Windows this is best done via [nvm](https://github.com/coreybutler/nvm-windows/releases).

After `nvm` has been installed, execute the command `nvm list available` to see which versions of `Node.js` are
available to install, now install a compatible version of `Node.js` e.g. `nvm install 21.7.2`.  
Now use `nvm` to select the updated `Node.js` version e.g. `nvm use 21.7.2`.

Now install the Postman Collections Runner `Newman` via: `npm install -g newman`.  
And the additional Newman HTML reporting feature: `npm install -g newman-reporter-htmlextra`

To verify your installation execute the following command and check that the version is 6.1.2 or greater:  
`newman -version`

**Note:** You may need to temporarily disable npm SSL: `npm config set strict-ssl false`