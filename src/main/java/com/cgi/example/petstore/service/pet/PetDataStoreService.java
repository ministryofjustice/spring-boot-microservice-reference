package com.cgi.example.petstore.service.pet;

import com.cgi.example.petstore.exception.NotFoundException;
import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetPatchRequest;
import com.cgi.example.petstore.model.PetResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Design decision: This class acts as a Facade between the PetService and the MongoDB
 * PetRepository. Specifically the persistence implementation (PetDocument, PetRepository) is not
 * visible (no imports) outside of this class/package. This allows the persistence implementation to
 * be altered and only the PetDataStoreService will be impacted.
 *
 * <p>Design decision: Since this is a service class it has no direct dependencies or knowledge of
 * any REST/HTTP concepts. Specifically instead of returning a ResponseEntity with a 404/NotFound
 * HTTP status code we throw a NotFoundException.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PetDataStoreService {

  private final PetMapper petMapper;
  private final PetRepository petRepository;

  public PetResponse insertNewPet(NewPetRequest newPet) {
    PetResponse pet = petMapper.mapToPetResponse(newPet);
    PetDocument petDocument = petMapper.mapToPetDocument(pet);

    PetDocument insertedPetDocument = petRepository.insert(petDocument);

    PetResponse insertedPet = petMapper.mapToPetResponse(insertedPetDocument);
    log.debug("Successfully inserted Pet with Id: [{}]", insertedPet.getPetId());
    return insertedPet;
  }

  public PetResponse findPetById(String id) {
    PetDocument petDocument = retrievePetDocument(id);
    return petMapper.mapToPetResponse(petDocument);
  }

  private PetDocument retrievePetDocument(String petId) {
    Optional<PetDocument> petDocumentOptional =
        petRepository.findByPersistenceStatusAndPetId(PersistenceStatus.ACTIVE.getValue(), petId);
    if (petDocumentOptional.isEmpty()) {
      String message = "Unable to find the pet with Id: [%s]".formatted(petId);
      throw new NotFoundException(message);
    }

    return petDocumentOptional.get();
  }

  public List<PetResponse> findPetsByStatus(List<PetAvailabilityStatus> statuses) {
    List<String> petDocumentStatuses = petMapper.mapToPetStatusStrings(statuses);
    List<PetDocument> petDocumentsWithMatchingStatus =
        petRepository.findByPersistenceStatusAndPetStatusIn(
            PersistenceStatus.ACTIVE.getValue(), petDocumentStatuses);

    return petMapper.mapToPets(petDocumentsWithMatchingStatus);
  }

  public PetResponse patch(PetPatchRequest petPatch) {
    PetResponse petToBePatched = findPetById(petPatch.getId());

    petMapper.updateTargetObjectFromSourceObject(petPatch, petToBePatched);
    PetDocument petDocumentToSave = petMapper.mapToPetDocument(petToBePatched);

    PetDocument insertedPetDocument = petRepository.save(petDocumentToSave);

    return petMapper.mapToPetResponse(insertedPetDocument);
  }

  public Optional<String> findOwnerCustomerIdForPet(String petId) {
    Optional<PetDocument> optionalPetDocument =
        petRepository.findByPersistenceStatusAndPetId(PersistenceStatus.ACTIVE.getValue(), petId);
    if (optionalPetDocument.isEmpty()) {
      return Optional.empty();
    }

    PetDocument petDocument = optionalPetDocument.get();

    return Optional.ofNullable(petDocument.getOwnerCustomerId());
  }

  public PetResponse updatePetWithNewOwner(String petId, String customerId) {
    PetDocument petDocument = retrievePetDocument(petId);
    petDocument.setOwnerCustomerId(customerId);
    petDocument.setPetStatus(PetAvailabilityStatus.PENDING_COLLECTION.getValue());

    PetDocument savedPetDocument = petRepository.save(petDocument);
    return findPetById(savedPetDocument.getPetId());
  }

  public String archivePet(String petId) {
    PetDocument petDocument = retrievePetDocument(petId);
    petDocument.setPersistenceStatus(PersistenceStatus.ARCHIVED.getValue());

    PetDocument archivedPetDocument = petRepository.save(petDocument);
    return "Successfully archived the Pet with Id: %s".formatted(archivedPetDocument.getPetId());
  }
}
