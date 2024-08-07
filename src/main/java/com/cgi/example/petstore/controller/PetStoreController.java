package com.cgi.example.petstore.controller;

import com.cgi.example.petstore.api.PetsApi;
import com.cgi.example.petstore.controller.validation.PetIdValidator;
import com.cgi.example.petstore.logging.aspects.LogMethodArguments;
import com.cgi.example.petstore.logging.aspects.LogMethodResponse;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.MultiplePetsResponse;
import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetDeletionResponse;
import com.cgi.example.petstore.model.PetPatchRequest;
import com.cgi.example.petstore.model.PetResponse;
import com.cgi.example.petstore.service.PetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PetStoreController implements PetsApi {

  private final PetIdValidator petIdValidator;
  private final PetService petService;

  @Override
  @LogMethodArguments
  @LogMethodResponse
  public ResponseEntity<PetResponse> addPet(NewPetRequest newPet) {
    PetResponse addedPet = petService.addToPetStore(newPet);

    return ResponseEntity.ok(addedPet);
  }

  @Override
  @LogMethodArguments
  @LogMethodResponse
  public ResponseEntity<PetDeletionResponse> deletePetById(String petId) {
    petIdValidator.validatePetId(petId);

    String message = petService.deletePetFromPetStore(petId);

    PetDeletionResponse petDeletionResponse = new PetDeletionResponse();
    petDeletionResponse.setPetId(petId);
    petDeletionResponse.setMessage(message);

    return ResponseEntity.ok(petDeletionResponse);
  }

  @Override
  @LogMethodArguments
  @LogMethodResponse
  public ResponseEntity<MultiplePetsResponse> findPetsByStatus(
      List<PetAvailabilityStatus> statuses) {
    List<PetResponse> pets = petService.retrieveAllPetsWithAStatusMatching(statuses);

    MultiplePetsResponse petsResponse = new MultiplePetsResponse();
    petsResponse.setPets(pets);

    return ResponseEntity.ok(petsResponse);
  }

  @Override
  @LogMethodArguments
  @LogMethodResponse
  public ResponseEntity<PetResponse> getPetById(String petId) {
    petIdValidator.validatePetId(petId);

    PetResponse pet = petService.retrievePetDetails(petId);

    return ResponseEntity.ok().body(pet);
  }

  @Override
  @LogMethodArguments
  @LogMethodResponse
  public ResponseEntity<PetResponse> purchasePet(String petId, CustomerRequest customer) {
    petIdValidator.validatePetId(petId);

    PetResponse purchasedPet = petService.purchase(petId, customer);

    return ResponseEntity.ok().body(purchasedPet);
  }

  @Override
  @LogMethodArguments
  @LogMethodResponse
  public ResponseEntity<PetResponse> patchPet(PetPatchRequest petPatch) {
    petIdValidator.validatePetId(petPatch.getId());

    PetResponse patchedPet = petService.patch(petPatch);

    return ResponseEntity.ok().body(patchedPet);
  }
}
