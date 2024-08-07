package com.cgi.example.petstore.exception.handler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cgi.example.petstore.exception.ApplicationException;
import com.cgi.example.petstore.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Tag("unit")
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void given_AbstractApplicationException_Should_ReturnPopulatedProblemDetail() {
    ApplicationException applicationException =
        new ApplicationException("Custom error message", HttpStatus.BAD_GATEWAY);

    ProblemDetail actualProblemDetail = handler.onApplicationException(applicationException);

    assertAll(
        () -> assertEquals(HttpStatus.BAD_GATEWAY.value(), actualProblemDetail.getStatus()),
        () ->
            assertEquals(
                "Handled by GlobalExceptionHandler: [Custom error message]",
                actualProblemDetail.getDetail()));
  }

  @Test
  void given_ValidationException_Should_ReturnPopulatedProblemDetail() {
    ValidationException validationException = new ValidationException("Validation failed");

    ProblemDetail actualProblemDetail = handler.onValidationException(validationException);

    assertAll(
        () -> assertEquals(HttpStatus.BAD_REQUEST.value(), actualProblemDetail.getStatus()),
        () ->
            assertEquals(
                "Handled by GlobalExceptionHandler: [Validation failed]",
                actualProblemDetail.getDetail()));
  }

  @Test
  void given_ConstraintViolationException_Should_ReturnPopulatedProblemDetail() {
    @SuppressWarnings("unchecked")
    ConstraintViolation<String> mockViolation = Mockito.mock(ConstraintViolation.class);
    ConstraintViolationException constraintViolationException =
        new ConstraintViolationException("A constraint violation occurred", Set.of(mockViolation));

    ProblemDetail actualProblemDetail =
        handler.onConstraintViolationException(constraintViolationException);

    assertAll(
        () -> assertEquals(HttpStatus.BAD_REQUEST.value(), actualProblemDetail.getStatus()),
        () ->
            assertEquals(
                "Handled by GlobalExceptionHandler: [A constraint violation occurred]",
                actualProblemDetail.getDetail()));
  }

  @Test
  void given_Throwable_Should_ReturnPopulatedProblemDetail() {
    Throwable throwable = new RuntimeException("Unexpected error");

    ProblemDetail actualProblemDetail = handler.onThrowable(throwable);

    assertAll(
        () ->
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actualProblemDetail.getStatus()),
        () ->
            assertEquals(
                "Handled by GlobalExceptionHandler: [An internal server error occurred.]",
                actualProblemDetail.getDetail()));
  }
}
