package com.cgi.example.petstore.controller.validation;

import com.cgi.example.petstore.exception.ValidationException;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class PetIdValidator {

  private static final String INVALID_PET_ID = "666";

  public void validatePetId(String petId) {
    if (Objects.isNull(petId)) {
      throw new ValidationException("Pet Id must not be null but found: null");
    }

    if (INVALID_PET_ID.equals(petId)) {
      String message =
          "Invalid Pet Id, the Id [%s] is not permitted, found: [%s]"
              .formatted(INVALID_PET_ID, petId);
      throw new ValidationException(message);
    }
  }
}
