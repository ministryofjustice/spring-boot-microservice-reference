package com.cgi.example.petstore.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.cgi.example.petstore.controller.validation.PetIdValidator;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.MultiplePetsResponse;
import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetDeletionResponse;
import com.cgi.example.petstore.model.PetPatchRequest;
import com.cgi.example.petstore.model.PetResponse;
import com.cgi.example.petstore.service.PetService;
import com.cgi.example.petstore.utils.TestData;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PetStoreControllerTest {

  @Mock private PetIdValidator validator;

  @Mock private PetService service;

  @InjectMocks private PetStoreController controller;

  private final TestData testData = new TestData();

  @Test
  void should_SuccessfullyAddPet() {
    final NewPetRequest newPetRequest = testData.createNewPetRequest();

    ResponseEntity<PetResponse> response = controller.addPet(newPetRequest);

    assertAll(
        () -> verify(service).addToPetStore(newPetRequest),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
  }

  @Test
  void should_SuccessfullyDeletePetById() {
    final String petId = "KT1546";

    ResponseEntity<PetDeletionResponse> response = controller.deletePetById(petId);

    assertAll(
        () -> verify(validator).validatePetId(petId),
        () -> verify(service).deletePetFromPetStore(petId),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
  }

  @Test
  void should_SuccessfullyFindPetsByStatus() {
    final List<PetAvailabilityStatus> statuses =
        List.of(PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE);

    ResponseEntity<MultiplePetsResponse> response = controller.findPetsByStatus(statuses);

    assertAll(
        () -> verify(service).retrieveAllPetsWithAStatusMatching(statuses),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
  }

  @Test
  void should_SuccessfullyGetPetById() {
    final String petId = "KT1546";

    ResponseEntity<PetResponse> response = controller.getPetById(petId);

    assertAll(
        () -> verify(validator).validatePetId(petId),
        () -> verify(service).retrievePetDetails(petId),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
  }

  @Test
  void should_SuccessfullyPurchasePet() {
    final String petId = "KT1546";
    final CustomerRequest customerRequest = testData.createCustomerRequest();

    ResponseEntity<PetResponse> response = controller.purchasePet(petId, customerRequest);

    assertAll(
        () -> verify(validator).validatePetId(petId),
        () -> verify(service).purchase(petId, customerRequest),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
  }

  @Test
  void should_SuccessfullyPatchPet() {
    final String petId = "KT1546";
    final PetPatchRequest petPatch = new PetPatchRequest();
    petPatch.setId(petId);

    ResponseEntity<PetResponse> response = controller.patchPet(petPatch);

    assertAll(
        () -> verify(validator).validatePetId(petId),
        () -> verify(service).patch(petPatch),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode()));
  }
}
