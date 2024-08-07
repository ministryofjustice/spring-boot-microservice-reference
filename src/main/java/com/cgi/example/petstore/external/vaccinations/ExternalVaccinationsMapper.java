package com.cgi.example.petstore.external.vaccinations;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.petstore.model.PetStoreVaccination;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ExternalVaccinationsMapper {

  public List<PetStoreVaccination> mapToPetStoreVaccinations(
      List<@Valid Vaccination> externalVaccinations) {
    if (Objects.isNull(externalVaccinations)) {
      return Collections.emptyList();
    }

    return externalVaccinations.stream()
        .map(this::mapToPetStoreVaccination)
        .collect(Collectors.toList());
  }

  private PetStoreVaccination mapToPetStoreVaccination(@Valid Vaccination externalVaccination) {
    PetStoreVaccination petStoreVaccination = new PetStoreVaccination();

    petStoreVaccination.setName(externalVaccination.getVaccinationName());
    petStoreVaccination.setDateOfAdminister(externalVaccination.getDateOfAdminister());

    return petStoreVaccination;
  }
}
