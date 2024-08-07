package com.cgi.example.petstore.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.cgi.example.petstore.utils.UriBuilder;
import com.jayway.jsonpath.JsonPath;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Tag("integration")
class ActuatorAndDocsIntegrationTest extends BaseIntegrationTest {

  @Test
  public void actuatorEndpointShouldListResources() {
    UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator");
    ResponseEntity<String> response = webClientExecutor.get(uri);

    Set<String> links = JsonPath.read(response.getBody(), "$._links.keys()");

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.OK),
        assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
        () ->
            assertThat(
                links,
                Matchers.containsInAnyOrder(
                    "self",
                    "beans",
                    "health",
                    "health-path",
                    "info",
                    "configprops",
                    "configprops-prefix",
                    "env",
                    "env-toMatch",
                    "loggers",
                    "loggers-name",
                    "metrics-requiredMetricName",
                    "metrics",
                    "mappings")));
  }

  @Test
  void actuatorHealthEndpointShouldShowUp() {
    UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator/health");
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String responseBody = response.getBody();
    assertAll(
        assertions.assertStatusCode(response, HttpStatus.OK),
        assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
        assertions.assertJsonPathEquals("UP", "$.status", responseBody),
        assertions.assertJsonPathEquals("UP", "$.components.ping.status", responseBody));
  }

  @Test
  void actuatorInfoEndpointShouldIncludeArtifactAndGitDetails() {
    UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator/info");

    ResponseEntity<String> response = webClientExecutor.get(uri);

    String responseBody = response.getBody();

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.OK),
        assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
        // JSON body, build attribute
        assertions.assertJsonPathEquals("21", "$.build.java.version", responseBody),
        assertions.assertJsonPathEquals(
            "Spring Boot Template Service modeled on an online Pet Store.",
            "$.build.description",
            responseBody),
        assertions.assertJsonPathEquals(
            "spring-boot-microservice-template", "$.build.artifact", responseBody),
        assertions.assertJsonPathEquals(
            "spring-boot-microservice-template", "$.build.name", responseBody),
        assertions.assertJsonPathEquals("com.cgi.example", "$.build.group", responseBody),
        // JSON body, git attribute
        () ->
            assertThat(
                JsonPath.<String>read(responseBody, "$.git.commit.id").length(),
                Matchers.equalTo(40)),
        () ->
            assertThat(
                JsonPath.read(responseBody, "$.git.build.version"),
                Matchers.not(Matchers.isEmptyOrNullString())),
        () ->
            assertThat(
                JsonPath.read(responseBody, "$.git.branch"),
                Matchers.not(Matchers.isEmptyOrNullString())),
        () ->
            assertThat(
                JsonPath.read(responseBody, "$.git.remote.origin.url"),
                Matchers.containsString("spring-boot-microservice-template.git")));
  }

  @ParameterizedTest
  @CsvSource({
    "v3/api-docs,application/json",
    "v3/api-docs.yaml,application/vnd.oai.openapi",
    "v3/api-docs/springdoc,application/json",
  })
  void should_ReturnApiDefinitionWhenCallingApiDocsEndpoints(
      String apiDocUrl, String expectedContentType) {
    UriComponentsBuilder uri = uriBuilder.getApplicationURIFor(apiDocUrl);
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String responseBody = response.getBody();
    assertAll(
        assertions.assertStatusCode(response, HttpStatus.OK),
        assertions.assertContentType(response, expectedContentType),
        assertions.assertContains(responseBody, UriBuilder.PET_STORE_BASE_URL + "/{petId}"),
        assertions.assertContains(responseBody, "Find pet by Id"),
        assertions.assertContains(responseBody, "Operations on the Pet Store concerning pets."));
  }

  @Test
  void actuatorMappingsEndpointShouldListMultipleMappings() {
    UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator/mappings");

    ResponseEntity<String> response = webClientExecutor.get(uri);

    int numberOfMappings =
        JsonPath.read(
            response.getBody(),
            "$.contexts.application.mappings.dispatcherServlets.dispatcherServlet.length()");

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.OK),
        assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
        () -> assertThat(numberOfMappings, Matchers.greaterThan(3)));
  }

  @Test
  void should_ReturnApiDefinitionWhenCallingApiDocsEndpoint() {
    UriComponentsBuilder uri = uriBuilder.getApplicationURIFor("v3/api-docs/swagger-config");
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String responseBody = response.getBody();

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertJsonPathEquals(1, "$.urls.length()", responseBody),
        assertions.assertJsonPathEquals("/v3/api-docs/springdoc", "$.urls[0].url", responseBody));
  }

  @Test
  void should_ReturnApiDefinition_When_CallingSwaggerUiIndexHtmlEndpoint() {
    UriComponentsBuilder uri = uriBuilder.getApplicationURIFor("swagger-ui/index.html");
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String responseBody = response.getBody();

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.OK),
        assertions.assertContentType(response, "text/html"),
        assertions.assertContains(responseBody, "swagger-ui"),
        assertions.assertContains(responseBody, "html"));
  }
}
