package com.cgi.example.petstore.external.vaccinations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@RequiredArgsConstructor
@Validated
@ConfigurationProperties(prefix = "external.apis.vaccinations")
public class VaccinationsConfiguration {

  private final String baseUrl;
  private final String path;
}
