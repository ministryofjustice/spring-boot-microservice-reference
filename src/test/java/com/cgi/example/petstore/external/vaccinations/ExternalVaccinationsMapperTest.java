package com.cgi.example.petstore.external.vaccinations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.petstore.model.PetStoreVaccination;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ExternalVaccinationsMapperTest {

  private ExternalVaccinationsMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ExternalVaccinationsMapper();
  }

  @Test
  void givenNullVaccinationsShouldMapToAnEmptyList() {
    List<PetStoreVaccination> actualVaccinations = mapper.mapToPetStoreVaccinations(null);

    assertNotNull(actualVaccinations);
    assertThat(actualVaccinations, Matchers.iterableWithSize(0));
  }

  @Test
  void givenThreeVaccinationsShouldMapToAListWithThreeVaccinations() {
    List<@Valid Vaccination> externalVaccinations =
        Arrays.asList(
            createVaccination("Parainfluenza", "2017-07-21"),
            createVaccination("Bordetella bronchiseptica", "2017-09-05"),
            createVaccination("Canine Adenovirus", "2016-01-25"));

    List<PetStoreVaccination> actualVaccinations =
        mapper.mapToPetStoreVaccinations(externalVaccinations);

    assertNotNull(actualVaccinations);
    assertThat(actualVaccinations, Matchers.iterableWithSize(3));
    List<String> vaccinationNames =
        actualVaccinations.stream().map(PetStoreVaccination::getName).collect(Collectors.toList());
    assertThat(
        vaccinationNames,
        Matchers.containsInAnyOrder(
            "Parainfluenza", "Bordetella bronchiseptica", "Canine Adenovirus"));
  }

  private Vaccination createVaccination(String vaccinationName, String dateOfAdminister) {
    Vaccination vaccination = new Vaccination();

    vaccination.setVaccinationName(vaccinationName);
    vaccination.setDateOfAdminister(LocalDate.parse(dateOfAdminister));

    return vaccination;
  }
}
