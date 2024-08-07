package com.cgi.example.petstore.local;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class SetSystemPropertiesForEmbeddedServicesTest {

  private static final String VACCINATIONS_URL_SYSTEM_PROPERTY = "VACCINATIONS_URL";
  private static final String MONGO_DB_URI_SYSTEM_PROPERTY = "MONGO_DB_URI";
  private static final String OAUTH_HOST_SYSTEM_PROPERTY = "OAUTH_HOST";

  @Mock private DynamicApplicationPropertiesRepository repository;

  @Mock private ConfigurableApplicationContext applicationContext;

  @InjectMocks private SetSystemPropertiesForEmbeddedServices dynamicConfiguration;

  @BeforeEach
  void setUp() {
    clearSystemProperties();
  }

  @AfterEach
  void tearDown() {
    clearSystemProperties();
  }

  private void clearSystemProperties() {
    System.clearProperty(VACCINATIONS_URL_SYSTEM_PROPERTY);
    System.clearProperty(MONGO_DB_URI_SYSTEM_PROPERTY);
    System.clearProperty(OAUTH_HOST_SYSTEM_PROPERTY);
  }

  @Test
  void should_PopulateSystemPropertiesFromDynamicApplicationProperties() {
    when(repository.getWireMockPort()).thenReturn(9050);
    when(repository.getMongoDBConnectionString()).thenReturn("mongodb://localhost:8456");
    when(repository.getOAuth2Host()).thenReturn("http://localhost:8000");

    assertNull(System.getProperty(VACCINATIONS_URL_SYSTEM_PROPERTY), "Failed precondition");
    assertNull(System.getProperty(MONGO_DB_URI_SYSTEM_PROPERTY), "Failed precondition");
    assertNull(System.getProperty(OAUTH_HOST_SYSTEM_PROPERTY), "Failed precondition");

    dynamicConfiguration.initialize(applicationContext);

    assertAll(
        () ->
            assertEquals(
                "http://localhost:9050", System.getProperty(VACCINATIONS_URL_SYSTEM_PROPERTY)),
        () ->
            assertEquals(
                "mongodb://localhost:8456", System.getProperty(MONGO_DB_URI_SYSTEM_PROPERTY)),
        () ->
            assertEquals("http://localhost:8000", System.getProperty(OAUTH_HOST_SYSTEM_PROPERTY)));
  }

  @Test
  void should_NotPopulateSystemProperties_WhenSystemPropertiesHaveAlreadyBeenSet() {
    final String presetUrl = "http://localhost:9999";
    System.setProperty(VACCINATIONS_URL_SYSTEM_PROPERTY, presetUrl);
    final String presetMongoDb = "mongodb://localhost:8888";
    System.setProperty(MONGO_DB_URI_SYSTEM_PROPERTY, presetMongoDb);
    final String presetOauthHost = "http://localhost:8000";
    System.setProperty(OAUTH_HOST_SYSTEM_PROPERTY, presetOauthHost);

    when(repository.getWireMockPort()).thenReturn(9050);
    when(repository.getMongoDBConnectionString()).thenReturn("mongodb://localhost:8456");
    when(repository.getMongoDBConnectionString()).thenReturn("mongodb://localhost:8456");
    when(repository.getOAuth2Host()).thenReturn("http://localhost:8000");

    assertNotNull(System.getProperty(VACCINATIONS_URL_SYSTEM_PROPERTY), "Failed precondition");
    assertNotNull(System.getProperty(MONGO_DB_URI_SYSTEM_PROPERTY), "Failed precondition");
    assertNotNull(System.getProperty(OAUTH_HOST_SYSTEM_PROPERTY), "Failed precondition");

    dynamicConfiguration.initialize(applicationContext);

    assertAll(
        () -> assertEquals(presetUrl, System.getProperty(VACCINATIONS_URL_SYSTEM_PROPERTY)),
        () -> assertEquals(presetMongoDb, System.getProperty(MONGO_DB_URI_SYSTEM_PROPERTY)),
        () -> assertEquals(presetOauthHost, System.getProperty(OAUTH_HOST_SYSTEM_PROPERTY)));
  }
}
