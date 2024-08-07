package com.cgi.example.petstore.external.vaccinations;

import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(VaccinationsConfiguration.class)
public class VaccinationsUri {

  private static final String VACCINATION_ID_PATH_VARIABLE = "vaccinationId";

  private final VaccinationsConfiguration vaccinationsConfiguration;

  public URI with(String vaccinationId) {

    URI baseUri = URI.create(vaccinationsConfiguration.getBaseUrl());
    return UriComponentsBuilder.newInstance()
        .uri(baseUri)
        .path(vaccinationsConfiguration.getPath())
        .build(Map.of(VACCINATION_ID_PATH_VARIABLE, vaccinationId));
  }
}
