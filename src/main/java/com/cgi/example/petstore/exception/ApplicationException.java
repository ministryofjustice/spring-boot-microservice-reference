package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatusCode;

public class ApplicationException extends RuntimeException {

  private final HttpStatusCode httpStatusCode;

  public ApplicationException(String message, HttpStatusCode httpStatusCode) {
    super(message);
    this.httpStatusCode = httpStatusCode;
  }

  public HttpStatusCode getHttpResponseCode() {
    return httpStatusCode;
  }

  public String getResponseMessage() {
    return getMessage();
  }
}
