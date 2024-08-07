package com.cgi.example.petstore.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.petstore.external.vaccinations.VaccinationsApiClient;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.PathTemplatePattern;
import com.github.tomakehurst.wiremock.matching.UrlPathTemplatePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Tag("integration")
class VaccinationsApiClientIntegrationTest extends BaseIntegrationTest {

  @Autowired private VaccinationsApiClient apiClient;

  @Test
  void should_ReturnVaccinationDetailsForValidVaccinationId() {
    Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations("AF54785412K");

    assertTrue(actualResponse.isPresent());
    List<Vaccination> vaccinations = actualResponse.get();
    assertThat(vaccinations, Matchers.iterableWithSize(3));
  }

  @Test
  void should_ReturnEmptyOptionalForUnknownVaccinationId() {
    Optional<List<Vaccination>> optionalVaccinations = apiClient.getVaccinations("Z6456INVALID");

    assertTrue(optionalVaccinations.isEmpty());
  }

  @Test
  void should_ReturnEmptyOptionalForEmptyVaccinationsResponse() {
    Optional<List<Vaccination>> optionalVaccinations = apiClient.getVaccinations("ZERO5485412K");

    assertTrue(optionalVaccinations.isEmpty());
  }

  @Test
  void should_RetryTwiceIfTheRequestFailsBeforeEventuallyFailing() {
    Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations("Z504INVALID");

    assertTrue(actualResponse.isEmpty());
    UrlPattern url = new UrlPattern(new PathTemplatePattern("/vaccinations/Z504INVALID"), false);
    wireMock().verify(3, newRequestPattern(RequestMethod.GET, url));
  }

  private ResponseDefinitionBuilder successResponse() {
    String body =
        fileUtils.readFile(
            "wiremock\\__files\\vaccinations\\multipleVaccinationsResponse_AF54785412K.json");

    return aResponse()
        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .withBody(body)
        .withStatus(HttpStatus.OK.value());
  }

  @Test
  void should_RetryTwiceIfTheRequestFailsBeforeEventuallySucceeding() {
    final String scenarioName = "RetryUntilSuccess";
    wireMock()
        .stubFor(
            WireMock.get(urlEqualTo("/vaccinations/RETRY54785412K"))
                .inScenario(scenarioName)
                .willSetStateTo("Second Call")
                .willReturn(aResponse().withStatus(HttpStatus.GATEWAY_TIMEOUT.value())));

    wireMock()
        .stubFor(
            WireMock.get(urlEqualTo("/vaccinations/RETRY54785412K"))
                .inScenario(scenarioName)
                .whenScenarioStateIs("Second Call")
                .willSetStateTo("Third Call")
                .willReturn(aResponse().withStatus(HttpStatus.GATEWAY_TIMEOUT.value())));

    wireMock()
        .stubFor(
            WireMock.get(urlEqualTo("/vaccinations/RETRY54785412K"))
                .inScenario(scenarioName)
                .whenScenarioStateIs("Third Call")
                .willReturn(successResponse()));

    Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations("RETRY54785412K");

    assertAll(
        () -> assertTrue(actualResponse.isPresent()),
        () -> assertThat(actualResponse.get(), Matchers.iterableWithSize(3)),
        () ->
            wireMock()
                .verify(
                    3,
                    newRequestPattern(
                        RequestMethod.GET,
                        new UrlPathTemplatePattern("/vaccinations/RETRY54785412K"))));
  }
}
