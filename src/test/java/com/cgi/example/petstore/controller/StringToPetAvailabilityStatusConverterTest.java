package com.cgi.example.petstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cgi.example.petstore.model.PetAvailabilityStatus;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("unit")
class StringToPetAvailabilityStatusConverterTest {

  private StringToPetAvailabilityStatusConverter converter;

  @BeforeEach
  void setUp() {
    converter = new StringToPetAvailabilityStatusConverter();
  }

  @ParameterizedTest
  @MethodSource("testDataProvider")
  void should_SuccessfullyConvertCaseInsensitiveStatusStringToEnum(
      String enumStringToConvert, PetAvailabilityStatus expectedStatus) {
    PetAvailabilityStatus actualStatus = converter.convert(enumStringToConvert);

    assertEquals(expectedStatus, actualStatus);
  }

  static Stream<Arguments> testDataProvider() {
    return Stream.of(
        Arguments.of("Available For Purchase", PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE),
        Arguments.of("Pending Collection", PetAvailabilityStatus.PENDING_COLLECTION),
        Arguments.of("pending collection", PetAvailabilityStatus.PENDING_COLLECTION),
        Arguments.of("Sold", PetAvailabilityStatus.SOLD),
        Arguments.of("SolD", PetAvailabilityStatus.SOLD),
        Arguments.of("SOLD", PetAvailabilityStatus.SOLD));
  }

  @Test
  void should_ThrowExceptionForInvalidPetAvailabilityStatusString() {
    IllegalArgumentException actualException =
        assertThrows(
            IllegalArgumentException.class, () -> converter.convert("Invalid Status Value"));

    assertEquals("Unexpected value 'Invalid Status Value'", actualException.getMessage());
  }
}
