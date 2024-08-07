package com.cgi.example.petstore.external.vaccinations;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class VaccinationsUriTest {

  private VaccinationsUri vaccinationsUri;

  @BeforeEach
  void setUp() {
    String baseUrl = "http://localhost:9090";
    String path = "vaccinations/{vaccinationId}";
    VaccinationsConfiguration vaccinationsConfiguration =
        new VaccinationsConfiguration(baseUrl, path);
    vaccinationsUri = new VaccinationsUri(vaccinationsConfiguration);
  }

  @Test
  void should_SuccessfullyCreateUriWithPopulatedVaccinationId() {
    URI uri = vaccinationsUri.with("AF54785412K");

    assertAll(
        () -> assertEquals("http", uri.getScheme()),
        () -> assertEquals("localhost", uri.getHost()),
        () -> assertEquals(9090, uri.getPort()),
        () -> assertEquals("/vaccinations/AF54785412K", uri.getPath()));
  }
}
