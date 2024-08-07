package com.cgi.example.petstore.service;

import com.cgi.example.petstore.external.vaccinations.VaccinationsService;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.CustomerResponse;
import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetPatchRequest;
import com.cgi.example.petstore.model.PetResponse;
import com.cgi.example.petstore.model.PetStoreVaccination;
import com.cgi.example.petstore.service.customer.CustomerDataStoreService;
import com.cgi.example.petstore.service.pet.PetDataStoreService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

  private final VaccinationsService vaccinationsService;
  private final PetDataStoreService petDataStoreService;
  private final CustomerDataStoreService customerDataStoreService;

  public PetResponse addToPetStore(NewPetRequest newPet) {
    PetResponse savedPet = petDataStoreService.insertNewPet(newPet);

    return enrichWithAdditionalInformation(savedPet);
  }

  public PetResponse retrievePetDetails(String petId) {
    PetResponse foundPet = petDataStoreService.findPetById(petId);

    return enrichWithAdditionalInformation(foundPet);
  }

  public List<PetResponse> retrieveAllPetsWithAStatusMatching(
      List<PetAvailabilityStatus> statuses) {
    List<PetResponse> petsMatchingStatus = petDataStoreService.findPetsByStatus(statuses);

    return petsMatchingStatus.stream()
        .map(this::enrichWithAdditionalInformation)
        .collect(Collectors.toList());
  }

  public PetResponse patch(PetPatchRequest pet) {
    PetResponse patchedPet = petDataStoreService.patch(pet);
    log.debug("Successfully patched the pet with petId [{}]", patchedPet.getPetId());
    return enrichWithAdditionalInformation(patchedPet);
  }

  public PetResponse purchase(String petId, CustomerRequest customer) {
    CustomerResponse savedCustomer = customerDataStoreService.insertIfAbsent(customer);

    PetResponse purchasedPet =
        petDataStoreService.updatePetWithNewOwner(petId, savedCustomer.getCustomerId());

    return enrichWithAdditionalInformation(purchasedPet);
  }

  private PetResponse enrichWithAdditionalInformation(PetResponse pet) {
    List<PetStoreVaccination> vaccinations =
        vaccinationsService.getVaccinationDetails(pet.getVaccinationId());

    pet.setVaccinations(vaccinations);

    Optional<String> optionalCustomerId =
        petDataStoreService.findOwnerCustomerIdForPet(pet.getPetId());
    if (optionalCustomerId.isPresent()) {
      CustomerResponse customerResponse =
          customerDataStoreService.retrieveCustomer(optionalCustomerId.get());
      pet.setOwner(customerResponse);
    }
    return pet;
  }

  public String deletePetFromPetStore(String petId) {
    return petDataStoreService.archivePet(petId);
  }
}
