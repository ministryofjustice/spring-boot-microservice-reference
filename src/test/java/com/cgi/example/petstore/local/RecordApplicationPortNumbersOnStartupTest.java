package com.cgi.example.petstore.local;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecordApplicationPortNumbersOnStartupTest {

  private RecordApplicationPortNumbersOnStartup recordPortNumbers;

  @BeforeEach
  void setUp() {
    recordPortNumbers = new RecordApplicationPortNumbersOnStartup();
  }

  @AfterEach
  void tearDown() {}

  @Test
  void onApplicationEvent() {
    // recordPortNumbers.onApplicationEvent();
  }
}
