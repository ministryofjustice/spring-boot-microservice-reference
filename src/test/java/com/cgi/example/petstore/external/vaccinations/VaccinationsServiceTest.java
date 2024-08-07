package com.cgi.example.petstore.external.vaccinations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.petstore.model.PetStoreVaccination;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class VaccinationsServiceTest {

  private static final String VACCINATION_ID = "AF54785412K";

  @Mock private ExternalVaccinationsMapper mapper;

  @Mock private VaccinationsApiClient apiClient;

  @InjectMocks private VaccinationsService service;

  @Test
  void when_ApiClientReturnsEmptyVaccinations_Should_ReturnEmptyList() {
    when(apiClient.getVaccinations(VACCINATION_ID)).thenReturn(Optional.empty());

    List<PetStoreVaccination> vaccinations = service.getVaccinationDetails(VACCINATION_ID);

    assertThat(vaccinations, Matchers.emptyIterable());
  }

  @Test
  void when_ApiClientReturnsVaccinations_Should_ReturnPopulatedList() {
    final List<Vaccination> externalVaccinations =
        List.of(createVaccination("Parainfluenza"), createVaccination("Bordetella bronchiseptica"));

    when(apiClient.getVaccinations(VACCINATION_ID)).thenReturn(Optional.of(externalVaccinations));
    when(mapper.mapToPetStoreVaccinations(externalVaccinations))
        .thenReturn(List.of(new PetStoreVaccination(), new PetStoreVaccination()));

    List<PetStoreVaccination> vaccinations = service.getVaccinationDetails(VACCINATION_ID);

    assertThat(vaccinations, Matchers.iterableWithSize(2));
  }

  private @NotNull Vaccination createVaccination(String vaccinationName) {
    Vaccination vaccination = new Vaccination();
    vaccination.setVaccinationName(vaccinationName);
    vaccination.setDateOfAdminister(LocalDate.parse("2017-07-21"));
    return vaccination;
  }
}
