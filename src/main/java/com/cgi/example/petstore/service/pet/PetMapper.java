package com.cgi.example.petstore.service.pet;

import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetInformationItem;
import com.cgi.example.petstore.model.PetPatchRequest;
import com.cgi.example.petstore.model.PetResponse;
import com.cgi.example.petstore.model.PetType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

  public PetResponse mapToPetResponse(NewPetRequest newPetToMap) {
    if (newPetToMap == null) {
      return null;
    }

    PetResponse pet = new PetResponse();

    pet.setVaccinationId(newPetToMap.getVaccinationId());
    pet.setName(newPetToMap.getName());
    pet.setPetType(newPetToMap.getPetType());
    List<String> list = newPetToMap.getPhotoUrls();
    if (list != null) {
      pet.setPhotoUrls(new ArrayList<>(list));
    }
    List<PetInformationItem> additionalInformation = newPetToMap.getAdditionalInformation();
    if (additionalInformation != null) {
      pet.setAdditionalInformation(new ArrayList<>(additionalInformation));
    }

    pet.setAvailabilityStatus(PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE);

    return pet;
  }

  public void updateTargetObjectFromSourceObject(
      PetPatchRequest patchToApply, PetResponse targetPet) {
    if (patchToApply == null) {
      return;
    }

    if (patchToApply.getAvailabilityStatus() != null) {
      targetPet.setAvailabilityStatus(patchToApply.getAvailabilityStatus());
    }
    if (patchToApply.getVaccinationId() != null) {
      targetPet.setVaccinationId(patchToApply.getVaccinationId());
    }
    if (patchToApply.getName() != null) {
      targetPet.setName(patchToApply.getName());
    }
    if (patchToApply.getPetType() != null) {
      targetPet.setPetType(patchToApply.getPetType());
    }
    List<String> list = patchToApply.getPhotoUrls();
    if (targetPet.getPhotoUrls() != null) {
      if (list != null) {
        targetPet.getPhotoUrls().clear();
        targetPet.getPhotoUrls().addAll(list);
      }
    } else {
      if (list != null) {
        targetPet.setPhotoUrls(new ArrayList<>(list));
      }
    }
    List<PetInformationItem> list1 = patchToApply.getAdditionalInformation();
    if (targetPet.getAdditionalInformation() != null) {
      if (list1 != null) {
        targetPet.getAdditionalInformation().clear();
        targetPet.getAdditionalInformation().addAll(list1);
      }
    } else {
      if (list1 != null) {
        targetPet.setAdditionalInformation(new ArrayList<>(list1));
      }
    }
  }

  public PetDocument mapToPetDocument(PetResponse petToMap) {
    if (petToMap == null) {
      return null;
    }

    PetDocument.PetDocumentBuilder petDocument = PetDocument.builder();

    petDocument.petId(petToMap.getPetId());
    petDocument.vaccinationId(petToMap.getVaccinationId());
    petDocument.name(petToMap.getName());
    if (petToMap.getPetType() != null) {
      petDocument.petType(petToMap.getPetType().name());
    }
    List<String> list = petToMap.getPhotoUrls();
    if (list != null) {
      petDocument.photoUrls(new ArrayList<>(list));
    }
    petDocument.additionalInformation(
        petInformationItemListToPetInformationItemPersistenceTypeList(
            petToMap.getAdditionalInformation()));
    petDocument.petStatus(mapToString(petToMap.getAvailabilityStatus()));
    petDocument.persistenceStatus(PersistenceStatus.ACTIVE.getValue());

    return petDocument.build();
  }

  public PetResponse mapToPetResponse(PetDocument petDocumentToMap) {
    if (petDocumentToMap == null) {
      return null;
    }

    PetResponse pet = new PetResponse();

    pet.setPetId(petDocumentToMap.getPetId());
    pet.setAvailabilityStatus(mapToPetStatus(petDocumentToMap.getPetStatus()));
    pet.setVaccinationId(petDocumentToMap.getVaccinationId());
    pet.setName(petDocumentToMap.getName());
    pet.setPetType(mapToPetType(petDocumentToMap.getPetType()));
    List<String> list = petDocumentToMap.getPhotoUrls();
    if (list != null) {
      pet.setPhotoUrls(new ArrayList<>(list));
    }
    pet.setAdditionalInformation(
        petInformationItemPersistenceTypeListToPetInformationItemList(
            petDocumentToMap.getAdditionalInformation()));

    return pet;
  }

  public List<PetResponse> mapToPets(List<PetDocument> petDocuments) {
    if (petDocuments == null) {
      return null;
    }

    List<PetResponse> list = new ArrayList<>(petDocuments.size());
    for (PetDocument petDocument : petDocuments) {
      list.add(mapToPetResponse(petDocument));
    }

    return list;
  }

  public List<String> mapToPetStatusStrings(List<PetAvailabilityStatus> petStatuses) {
    if (petStatuses == null) {
      return null;
    }

    List<String> list = new ArrayList<>(petStatuses.size());
    for (PetAvailabilityStatus petStatus : petStatuses) {
      list.add(mapToString(petStatus));
    }

    return list;
  }

  protected PetInformationItemPersistenceType petInformationItemToPetInformationItemPersistenceType(
      PetInformationItem petInformationItem) {
    if (petInformationItem == null) {
      return null;
    }

    PetInformationItemPersistenceType.PetInformationItemPersistenceTypeBuilder
        petInformationItemPersistenceType = PetInformationItemPersistenceType.builder();

    petInformationItemPersistenceType.name(petInformationItem.getName());
    petInformationItemPersistenceType.description(petInformationItem.getDescription());

    return petInformationItemPersistenceType.build();
  }

  protected List<PetInformationItemPersistenceType>
      petInformationItemListToPetInformationItemPersistenceTypeList(
          List<PetInformationItem> petInformationItems) {
    if (petInformationItems == null) {
      return null;
    }

    List<PetInformationItemPersistenceType> persistenceTypes =
        new ArrayList<>(petInformationItems.size());
    for (PetInformationItem petInformationItem : petInformationItems) {
      persistenceTypes.add(
          petInformationItemToPetInformationItemPersistenceType(petInformationItem));
    }

    return persistenceTypes;
  }

  protected PetInformationItem petInformationItemPersistenceTypeToPetInformationItem(
      PetInformationItemPersistenceType petInformationItemPersistenceType) {
    if (petInformationItemPersistenceType == null) {
      return null;
    }

    PetInformationItem petInformationItem = new PetInformationItem();

    petInformationItem.setName(petInformationItemPersistenceType.getName());
    petInformationItem.setDescription(petInformationItemPersistenceType.getDescription());

    return petInformationItem;
  }

  protected List<PetInformationItem> petInformationItemPersistenceTypeListToPetInformationItemList(
      List<PetInformationItemPersistenceType> list) {
    if (list == null) {
      return null;
    }

    List<PetInformationItem> list1 = new ArrayList<>(list.size());
    for (PetInformationItemPersistenceType petInformationItemPersistenceType : list) {
      list1.add(
          petInformationItemPersistenceTypeToPetInformationItem(petInformationItemPersistenceType));
    }

    return list1;
  }

  String mapToString(PetAvailabilityStatus petStatus) {
    if (Objects.isNull(petStatus)) {
      return null;
    }
    return petStatus.getValue();
  }

  PetType mapToPetType(String petTypeString) {
    if (Objects.isNull(petTypeString)) {
      return null;
    }
    return EnumUtils.getEnumIgnoreCase(
        PetType.class, petTypeString.replace(StringUtils.SPACE, "_"));
  }

  PetAvailabilityStatus mapToPetStatus(String petStatusString) {
    if (Objects.isNull(petStatusString)) {
      return null;
    }
    return EnumUtils.getEnumIgnoreCase(
        PetAvailabilityStatus.class, petStatusString.replace(StringUtils.SPACE, "_"));
  }
}
