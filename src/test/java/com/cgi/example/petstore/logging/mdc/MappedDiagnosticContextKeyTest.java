package com.cgi.example.petstore.logging.mdc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import ch.qos.logback.classic.Level;
import com.cgi.example.petstore.utils.LoggingVerification;
import com.cgi.example.petstore.utils.TestLoggingTarget;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.MDC;

@ExtendWith(LoggingVerification.class)
@TestLoggingTarget(MappedDiagnosticContextKey.class)
@Tag("unit")
class MappedDiagnosticContextKeyTest {

  private static final Consumer<MappedDiagnosticContextKey> ASSERT_MDC_VALUE_IS_NULL =
      key -> {
        String actualMdcValue = MDC.get(key.getMdcKey());
        String message =
            "Expected null MDC value for key [%s] but found [%s]".formatted(key, actualMdcValue);
        assertNull(actualMdcValue, message);
      };

  @BeforeEach
  void beforeEach() {
    MDC.clear();
  }

  @AfterEach
  void afterEach() {
    MDC.clear();
  }

  @ParameterizedTest
  @MethodSource("allMappedDiagnosticContextKeys")
  void should_StoreValueInTheMdc_whenCallingPut(MappedDiagnosticContextKey key) {
    assertNull(MDC.get(key.getMdcKey()), "Failed precondition for key: " + key);
    final String expectedNewValue = "newValue";

    key.put(expectedNewValue);

    assertEquals(expectedNewValue, MDC.get(key.getMdcKey()));

    String expectedLogMessage =
        "Populated the MDC key %s with a value of [%s]"
            .formatted(key.getMdcKey(), expectedNewValue);
    LoggingVerification.assertLog(Level.DEBUG, Matchers.equalTo(expectedLogMessage));
  }

  @ParameterizedTest
  @MethodSource("allMappedDiagnosticContextKeys")
  void should_NotStoreValueInTheMdc_whenCallingPutWithNullValue(MappedDiagnosticContextKey key) {
    assertNull(MDC.get(key.getMdcKey()), "Failed precondition for key: " + key);

    key.put(null);

    assertNull(MDC.get(key.getMdcKey()));
  }

  @ParameterizedTest
  @MethodSource("allMappedDiagnosticContextKeys")
  void allMdcKeysShouldBeNonNullAndNotEmpty(MappedDiagnosticContextKey key) {
    String actualMdcKey = key.getMdcKey();

    assertThat(actualMdcKey, not(Matchers.isEmptyOrNullString()));
  }

  @Test
  void clearAll_should_clearAllMdcKeys() {
    allMappedDiagnosticContextKeys().forEach(ASSERT_MDC_VALUE_IS_NULL);

    allMappedDiagnosticContextKeys().forEach(key -> key.put("newValue"));

    MappedDiagnosticContextKey.clearAll();

    allMappedDiagnosticContextKeys().forEach(ASSERT_MDC_VALUE_IS_NULL);
    LoggingVerification.assertLog(Level.DEBUG, Matchers.equalTo("Clearing all MDC keys"));
  }

  static Stream<MappedDiagnosticContextKey> allMappedDiagnosticContextKeys() {
    return Arrays.stream(MappedDiagnosticContextKey.values());
  }
}
