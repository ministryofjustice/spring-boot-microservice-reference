package com.cgi.example.petstore.service.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetResponse;
import com.cgi.example.petstore.model.PetType;
import com.cgi.example.petstore.service.pet.PetDocument;
import com.cgi.example.petstore.service.pet.PetMapper;
import com.cgi.example.petstore.utils.TestData;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class PetMapperTest {

  private final TestData testData = new TestData();

  private PetMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new PetMapper();
  }

  @Test
  void should_SuccessfullyMapFromAPetDocumentToPet() {
    PetResponse actualPet = mapper.mapToPetResponse(testData.createPetDocument());

    assertNotNull(actualPet);
    assertAll(
        () -> assertNotNull(actualPet),
        () -> assertEquals("KT1546", actualPet.getPetId()),
        () -> assertEquals("AF54785412K", actualPet.getVaccinationId()),
        () -> assertEquals("Fido", actualPet.getName()),
        () -> assertEquals(PetType.DOG, actualPet.getPetType()),
        () ->
            assertEquals(
                PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE, actualPet.getAvailabilityStatus()),
        () ->
            assertThat(
                actualPet.getPhotoUrls(),
                Matchers.contains(
                    "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6")),
        () -> assertThat(actualPet.getVaccinations(), CoreMatchers.nullValue()),
        () -> assertThat(actualPet.getAdditionalInformation(), Matchers.empty()));
  }

  @Test
  void should_SuccessfullyMapFromAPetToPetDocument() {
    PetResponse pet = testData.createPetResponse();

    PetDocument actualPetDocument = mapper.mapToPetDocument(pet);

    assertNotNull(actualPetDocument);
    assertAll(
        () -> assertNotNull(actualPetDocument),
        () -> assertEquals("KT1546", actualPetDocument.getPetId()),
        () -> assertEquals("AF54785412K", actualPetDocument.getVaccinationId()),
        () -> assertEquals("Fido", actualPetDocument.getName()),
        () -> assertEquals(PetType.DOG.name(), actualPetDocument.getPetType()),
        () ->
            assertEquals(
                PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE.getValue(),
                actualPetDocument.getPetStatus()),
        () ->
            assertThat(
                actualPetDocument.getPhotoUrls(),
                Matchers.contains(
                    "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6")),
        () -> assertThat(actualPetDocument.getAdditionalInformation(), Matchers.empty()));
  }

  @Test
  void should_SuccessfullyMapFromANewPetToPet() {
    PetResponse actualPet = mapper.mapToPetResponse(testData.createNewPetRequest());

    assertNotNull(actualPet);
    assertAll(
        () -> assertNotNull(actualPet),
        () -> assertNull(actualPet.getPetId()),
        () -> assertEquals("AF54785412K", actualPet.getVaccinationId()),
        () -> assertEquals("Fido", actualPet.getName()),
        () -> assertEquals(PetType.DOG, actualPet.getPetType()),
        () ->
            assertEquals(
                PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE, actualPet.getAvailabilityStatus()),
        () ->
            assertThat(
                actualPet.getPhotoUrls(),
                Matchers.contains(
                    "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6")),
        () -> assertThat(actualPet.getVaccinations(), CoreMatchers.nullValue()),
        () -> assertThat(actualPet.getAdditionalInformation(), Matchers.hasSize(1)),
        () ->
            assertThat(
                actualPet.getAdditionalInformation(),
                Matchers.contains(testData.createPetInformationItem("Personality", "Energetic"))));
  }
}
