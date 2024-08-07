package com.cgi.example.petstore.controller.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cgi.example.petstore.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class PetIdValidatorTest {

  private PetIdValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PetIdValidator();
  }

  @Test
  void should_ThrowValidationException_IfPetIdIsNull() {
    ValidationException actualException =
        assertThrows(ValidationException.class, () -> validator.validatePetId(null));

    assertEquals("Pet Id must not be null but found: null", actualException.getMessage());
  }

  @Test
  void should_ThrowValidationException_IfPetIdIsInvalid() {
    final String invalidPetId = "666";
    ValidationException actualException =
        assertThrows(
            ValidationException.class,
            () -> validator.validatePetId(invalidPetId),
            "Expected validatePetId(666) to throw, but it didn't");

    assertTrue(
        actualException
            .getMessage()
            .contains("Invalid Pet Id, the Id [666] is not permitted, found: [666]"));
  }

  @Test
  void should_NotThrowException_IfPetIdIsValid() {
    String validPetId = "KT1546";
    assertDoesNotThrow(() -> validator.validatePetId(validPetId));
  }
}
