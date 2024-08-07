package com.cgi.example.petstore.local;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.petstore.config.PetStoreSystemProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Order(1)
@Component
public class SetSystemPropertiesForEmbeddedServices
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String DUMMY_CLIENT_ID = "DummyClientId";
  private static final String DUMMY_CLIENT_SECRET = "DummyClientSecret";
  private static final String WIRE_MOCK_HOST_FORMAT = "http://localhost:%d";

  private final DynamicApplicationPropertiesRepository propertiesRepository;

  /*
   A no-args constructor is required by Spring on application startup as this class is listed as a
   context.initializer.classes in the application-local.yaml config.
  */
  public SetSystemPropertiesForEmbeddedServices() {
    this(new DynamicApplicationPropertiesRepository());
  }

  public SetSystemPropertiesForEmbeddedServices(
      DynamicApplicationPropertiesRepository propertiesRepository) {
    this.propertiesRepository = propertiesRepository;
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    log.info("About to set System Properties for local embedded services");

    configureWireMock(propertiesRepository);
    configureMongoDB(propertiesRepository);
    configureOAuth2(propertiesRepository);

    log.info("Completed setting System Properties for local embedded services");
  }

  private void configureMongoDB(DynamicApplicationPropertiesRepository propertiesRepository) {
    String mongoDBConnectionString = propertiesRepository.getMongoDBConnectionString();
    PetStoreSystemProperty.MONGO_DB_URI.setSystemPropertyIfAbsent(mongoDBConnectionString);
  }

  private void configureWireMock(DynamicApplicationPropertiesRepository propertiesRepository) {
    String newSystemPropertyValue =
        WIRE_MOCK_HOST_FORMAT.formatted(propertiesRepository.getWireMockPort());
    PetStoreSystemProperty.VACCINATIONS_URL.setSystemPropertyIfAbsent(newSystemPropertyValue);
  }

  private void configureOAuth2(DynamicApplicationPropertiesRepository propertiesRepository) {
    String oAuth2Host = propertiesRepository.getOAuth2Host();
    PetStoreSystemProperty.OAUTH_HOST.setSystemPropertyIfAbsent(oAuth2Host);

    PetStoreSystemProperty.OAUTH_CLIENT_ID.setSystemPropertyIfAbsent(DUMMY_CLIENT_ID);
    PetStoreSystemProperty.OAUTH_CLIENT_SECRET.setSystemPropertyIfAbsent(DUMMY_CLIENT_SECRET);
  }
}
