package com.cgi.example.petstore.external.vaccinations;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.external.animalvaccination.model.VaccinationsResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaccinationsApiClient {

  private final WebClient webClient;
  private final VaccinationsUri vaccinationsUri;

  public Optional<List<Vaccination>> getVaccinations(String vaccinationId) {
    URI uri = vaccinationsUri.with(vaccinationId);

    log.debug("About to get vaccinations with URI: [{}]", uri);

    try {
      ResponseEntity<VaccinationsResponse> vaccinationsResponse = getVaccinationsResponse(uri);
      Optional<List<@Valid Vaccination>> vaccinationsOptional =
          extractVaccinations(vaccinationsResponse);

      if (vaccinationsOptional.isEmpty()) {
        log.info("Unable to determine vaccinations for vaccinationId: [{}]", vaccinationId);
      } else {
        log.debug(
            "Retrieved {} vaccinations with URI: [{}]", vaccinationsOptional.get().size(), uri);
      }

      return vaccinationsOptional;

    } catch (RuntimeException e) {
      log.info(
          "Unable to determine the vaccinations for vaccinationId: [{}] due to error [{}]",
          vaccinationId,
          e.getMessage(),
          e);
      return Optional.empty();
    }
  }

  private Optional<List<@Valid Vaccination>> extractVaccinations(
      ResponseEntity<VaccinationsResponse> vaccinationsResponse) {
    if (Objects.isNull(vaccinationsResponse)
        || !vaccinationsResponse.getStatusCode().is2xxSuccessful()
        || Objects.isNull(vaccinationsResponse.getBody())
        || Objects.isNull(vaccinationsResponse.getBody().getVaccinations())) {
      return Optional.empty();
    }

    return Optional.of(vaccinationsResponse.getBody().getVaccinations());
  }

  private ResponseEntity<VaccinationsResponse> getVaccinationsResponse(URI uri)
      throws WebClientException {

    return webClient
        .get()
        .uri(uri)
        .retrieve()
        .toEntity(VaccinationsResponse.class)
        .retryWhen(Retry.backoff(2, Duration.of(200, ChronoUnit.MILLIS)))
        .block();
  }
}
