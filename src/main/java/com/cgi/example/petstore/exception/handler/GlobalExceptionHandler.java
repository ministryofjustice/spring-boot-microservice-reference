package com.cgi.example.petstore.exception.handler;

import com.cgi.example.petstore.exception.ApplicationException;
import com.cgi.example.petstore.exception.ValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ProblemDetail onApplicationException(ApplicationException exception) {
    ProblemDetail problemDetail =
        createProblemDetail(exception.getResponseMessage(), exception.getHttpResponseCode());

    log.info("An AbstractApplicationException occurred: [{}]", exception.getMessage(), exception);
    return problemDetail;
  }

  @ExceptionHandler(ValidationException.class)
  public ProblemDetail onValidationException(ValidationException exception) {
    ProblemDetail problemDetail =
        createProblemDetail(exception.getMessage(), HttpStatus.BAD_REQUEST);

    log.info("A ValidationException occurred: [{}]", exception.getMessage(), exception);
    return problemDetail;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail onConstraintViolationException(ConstraintViolationException exception) {
    ProblemDetail problemDetail =
        createProblemDetail(exception.getMessage(), HttpStatus.BAD_REQUEST);

    log.info("A ConstraintViolationException occurred: [{}]", exception.getMessage(), exception);
    return problemDetail;
  }

  @ExceptionHandler(Throwable.class)
  public ProblemDetail onThrowable(Throwable throwable) {
    ProblemDetail problemDetail =
        createProblemDetail("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);

    log.info("A Throwable occurred: [{}]", throwable.getMessage(), throwable);
    return problemDetail;
  }

  private ProblemDetail createProblemDetail(String detail, HttpStatusCode httpStatus) {
    String simpleClassName = getClass().getSimpleName();
    String detailedMessage = "Handled by %s: [%s]".formatted(simpleClassName, detail);

    return ProblemDetail.forStatusAndDetail(httpStatus, detailedMessage);
  }
}
