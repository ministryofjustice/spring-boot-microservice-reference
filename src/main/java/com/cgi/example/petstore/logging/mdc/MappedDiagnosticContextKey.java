package com.cgi.example.petstore.logging.mdc;

import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Getter
@Slf4j
public enum MappedDiagnosticContextKey {
  REQUEST_ID("requestId"),
  USERNAME("username");

  private final String mdcKey;

  MappedDiagnosticContextKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  public static void clearAll() {
    log.debug("Clearing all MDC keys");
    MDC.clear();
  }

  public void put(String value) {
    if (Objects.nonNull(value)) {
      MDC.put(mdcKey, value);
      log.debug("Populated the MDC key {} with a value of [{}]", mdcKey, value);
    }
  }
}
