package com.cgi.example.petstore.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

@Disabled
public class LoggingVerification implements BeforeEachCallback, AfterEachCallback {

  private static LoggingListAppender listAppender;
  private Logger testLogger;

  public static void assertLog(Level expectedLogLevel, Matcher<String> logMessageMatcher) {
    List<ILoggingEvent> allLogEvents = listAppender.getLogEvents();
    List<ILoggingEvent> matchingLogLevel =
        allLogEvents.stream().filter(event -> event.getLevel().equals(expectedLogLevel)).toList();

    List<ILoggingEvent> matchingLogEvents =
        matchingLogLevel.stream()
            .filter(event -> logMessageMatcher.matches(event.getFormattedMessage()))
            .toList();

    if (matchingLogEvents.isEmpty()) {
      String message =
          "Unable to find a %s log event with message matching %s in %s"
              .formatted(expectedLogLevel, logMessageMatcher, allLogEvents);
      fail(message);
    }

    if (matchingLogEvents.size() > 1) {
      String message =
          "Found %d %s log events with message matching %s, but expected 1, in %s"
              .formatted(
                  matchingLogEvents.size(), expectedLogLevel, logMessageMatcher, allLogEvents);
      fail(message);
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    testLogger.detachAppender(listAppender);
    listAppender.stop();
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    TestLoggingTarget loggingTarget =
        context.getRequiredTestClass().getAnnotation(TestLoggingTarget.class);
    assertNotNull(
        loggingTarget,
        "Expected non-null test logging target to be defined by the test class annotation @%s()"
            .formatted(TestLoggingTarget.class.getSimpleName()));

    testLogger = (Logger) LoggerFactory.getLogger(loggingTarget.value());
    testLogger.setLevel(Level.DEBUG);

    listAppender = new LoggingListAppender();
    listAppender.start();

    testLogger.addAppender(listAppender);
  }

  @Getter
  @Slf4j
  private static final class LoggingListAppender extends AppenderBase<ILoggingEvent> {
    private final List<ch.qos.logback.classic.spi.ILoggingEvent> logEvents =
        new CopyOnWriteArrayList<>();

    public LoggingListAppender() {
      setName("LoggingVerificationListAppender");
    }

    @Override
    protected void append(ILoggingEvent loggingEvent) {
      log.info("Appending the Logging Event: [{}]", loggingEvent);
      logEvents.add(loggingEvent);
    }
  }
}
