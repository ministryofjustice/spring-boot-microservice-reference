package com.cgi.example.petstore.utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import org.junit.jupiter.api.Disabled;

@Disabled("Not a test class")
public class ProcessManagement {

  private static final long THREE_SECOND_TIME_OUT = 3;

  public static void waitUntil(Supplier<Boolean> isComplete) {
    final long startTime = System.currentTimeMillis();

    for (; ; ) {
      // Sleep until complete or timeout is reached
      sleepForMilliseconds(250);
      Duration duration = Duration.of(System.currentTimeMillis() - startTime, ChronoUnit.MILLIS);

      if (isComplete.get() || duration.getSeconds() > THREE_SECOND_TIME_OUT) {
        break;
      }
    }
  }

  private static void sleepForMilliseconds(int milliSeconds) {
    try {
      Thread.sleep(milliSeconds);
    } catch (InterruptedException e) {
      String message = "ProcessManagement was interrupted: %s".formatted(e.getMessage());
      throw new RuntimeException(message, e);
    }
  }
}
