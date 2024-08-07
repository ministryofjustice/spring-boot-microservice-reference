package com.cgi.example.petstore.service.pet;

import lombok.Getter;

@Getter
public enum PersistenceStatus {
  ACTIVE("Active"),
  ARCHIVED("Archived");

  private final String value;

  PersistenceStatus(String value) {
    this.value = value;
  }

  public static PersistenceStatus fromValue(String value) {
    for (PersistenceStatus status : PersistenceStatus.values()) {
      if (status.value.equalsIgnoreCase(value)) {
        return status;
      }
    }
    String message = "Unexpected value '%s'".formatted(value);
    throw new IllegalArgumentException(message);
  }
}
