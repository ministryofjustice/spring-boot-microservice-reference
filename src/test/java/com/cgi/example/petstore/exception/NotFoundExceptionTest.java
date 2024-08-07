package com.cgi.example.petstore.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
class NotFoundExceptionTest {

  @Test
  void given_NotFoundException_should_HaveMessageAndNotFoundHttpStatusCode() {
    NotFoundException exception = new NotFoundException("Not found error message");

    assertAll(
        () -> assertEquals("Not found error message", exception.getMessage()),
        () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpResponseCode()));
  }
}
