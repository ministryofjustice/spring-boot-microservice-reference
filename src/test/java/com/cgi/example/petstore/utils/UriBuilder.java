package com.cgi.example.petstore.utils;

import java.util.Objects;
import org.junit.jupiter.api.Disabled;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Disabled("Not a test class")
public class UriBuilder {

  public static final String PET_STORE_BASE_URL = "api/v1/pet-store/pets";

  private final Environment environment;

  public UriBuilder(Environment environment) {
    this.environment = environment;
  }

  public UriComponentsBuilder getPetStoreURIFor(String resource) {
    return getApplicationURIFor(PET_STORE_BASE_URL).pathSegment(resource);
  }

  public UriComponentsBuilder getPetStoreBaseURI() {
    return getApplicationURIFor(PET_STORE_BASE_URL);
  }

  public UriComponentsBuilder getApplicationURIFor(String resource) {
    return getUriComponentsBuilder(getApplicationPort()).pathSegment(resource);
  }

  public UriComponentsBuilder getManagementURIFor(String resource) {
    return getUriComponentsBuilder(getManagementPort()).pathSegment(resource);
  }

  private int getManagementPort() {
    return getPortNumberFromEnvironmentProperty("local.management.port");
  }

  private int getApplicationPort() {
    return getPortNumberFromEnvironmentProperty("local.server.port");
  }

  private int getPortNumberFromEnvironmentProperty(String environmentPropertyKey) {
    Integer portNumber = environment.getProperty(environmentPropertyKey, Integer.class);

    if (Objects.isNull(portNumber) || portNumber <= 0) {
      String message =
          "Unable to determine the port number from [%s] this should have been set, but instead found [%d]"
              .formatted(environmentPropertyKey, portNumber);
      throw new IllegalStateException(message);
    }

    return portNumber;
  }

  private UriComponentsBuilder getUriComponentsBuilder(int port) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
    uriComponentsBuilder.scheme("http").host("localhost").port(port);

    return uriComponentsBuilder;
  }
}
