package com.cgi.example.petstore.utils;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ProcessManagementTest {

  @Test
  void should_TimeOutAfterThreeSecondsIsTaskHasNotCompleted() {
    assertTimeoutPreemptively(
        Duration.ofSeconds(5), () -> ProcessManagement.waitUntil(() -> false));
  }

  @Test
  void should_CompleteInLessThanTwoSecondsOnImmediateSuccess() {
    assertTimeoutPreemptively(Duration.ofSeconds(2), () -> ProcessManagement.waitUntil(() -> true));
  }
}
