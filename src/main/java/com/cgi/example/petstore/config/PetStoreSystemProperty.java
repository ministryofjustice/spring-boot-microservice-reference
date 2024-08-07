package com.cgi.example.petstore.config;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PetStoreSystemProperty {
  MONGO_DB_URI("MONGO_DB_URI"),
  OAUTH_CLIENT_ID("OAUTH_CLIENT_ID"),
  OAUTH_CLIENT_SECRET("OAUTH_CLIENT_SECRET"),
  OAUTH_HOST("OAUTH_HOST"),
  VACCINATIONS_URL("VACCINATIONS_URL");

  private final String systemProperty;

  PetStoreSystemProperty(String systemProperty) {
    this.systemProperty = systemProperty;
  }

  public String get() {
    return System.getProperty(systemProperty);
  }

  public void setSystemPropertyIfAbsent(String newSystemPropertyValue) {
    String existingPropertyValue = get();
    if (Objects.nonNull(existingPropertyValue)) {
      log.info(
          "Not setting system property {} as it has already been set with a value of [{}]",
          systemProperty,
          existingPropertyValue);
    } else {
      set(newSystemPropertyValue);
    }
  }

  private void set(String newSystemPropertyValue) {
    System.setProperty(systemProperty, newSystemPropertyValue);
  }

  public void clear() {
    System.clearProperty(systemProperty);
  }
}
