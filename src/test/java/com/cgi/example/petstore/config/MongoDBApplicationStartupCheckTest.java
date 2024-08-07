package com.cgi.example.petstore.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MongoDBApplicationStartupCheckTest {

  @Mock private MongoTemplate mockMongoTemplate;

  @Mock private ApplicationReadyEvent mockApplicationReadyEvent;

  private MongoDBApplicationStartupCheck startupCheck;

  @BeforeEach
  void setUp() {
    startupCheck = new MongoDBApplicationStartupCheck(mockMongoTemplate);
  }

  @Test
  void whenMongoDbIsAvailableShouldNotThrowAnException() {
    when(mockMongoTemplate.executeCommand("{ serverStatus: 1 }"))
        .thenReturn(new Document("ok", StringUtils.EMPTY));

    assertDoesNotThrow(
        () -> {
          startupCheck.onApplicationEvent(mockApplicationReadyEvent);
        });
  }

  @Test
  void whenMongoDbIsNotAvailableShouldThrowIllegalStateException() {
    when(mockMongoTemplate.executeCommand("{ serverStatus: 1 }")).thenReturn(new Document());

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> startupCheck.onApplicationEvent(mockApplicationReadyEvent));

    assertEquals("Unable to verify connectivity to MongoDB", exception.getMessage());
  }
}
