package com.cgi.example.petstore.external.vaccinations;

import com.cgi.example.petstore.model.PetStoreVaccination;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaccinationsService {

  private final ExternalVaccinationsMapper mapper;
  private final VaccinationsApiClient apiClient;

  public List<PetStoreVaccination> getVaccinationDetails(String vaccinationId) {
    Optional<List<com.cgi.example.external.animalvaccination.model.Vaccination>>
        vaccinationsOptional = apiClient.getVaccinations(vaccinationId);

    if (vaccinationsOptional.isEmpty()) {
      return Collections.emptyList();
    }

    return mapper.mapToPetStoreVaccinations(vaccinationsOptional.get());
  }
}
