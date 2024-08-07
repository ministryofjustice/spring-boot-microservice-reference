package com.cgi.example.petstore.logging.mdc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import com.cgi.example.petstore.utils.LoggingVerification;
import com.cgi.example.petstore.utils.TestLoggingTarget;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingVerification.class)
@TestLoggingTarget(AddUniqueRequestIdToMappedDiagnosticContextAndResponse.class)
@Tag("unit")
class AddUniqueRequestIdToMappedDiagnosticContextAndResponseTest {

  @Mock private HttpServletRequest mockHttpRequest;

  @Mock private HttpServletResponse mockHttpResponse;

  @Mock private FilterChain mockFilterChain;

  private AddUniqueRequestIdToMappedDiagnosticContextAndResponse filter;

  @BeforeEach
  void beforeEach() {
    MDC.clear();
    filter = new AddUniqueRequestIdToMappedDiagnosticContextAndResponse();
  }

  @AfterEach
  void afterEach() {
    MDC.clear();
  }

  @Test
  void should_PopulateTheRequestIdInTheMappedDiagnosticContext()
      throws ServletException, IOException {
    assertNull(getRequestIdFromTheMdc(), "Failed precondition");

    filter.doFilter(mockHttpRequest, mockHttpResponse, mockFilterChain);

    String actualRequestId = getRequestIdFromTheMdc();
    assertAll(
        () -> assertThat(actualRequestId, Matchers.not(Matchers.isEmptyOrNullString())),
        () -> assertThat(actualRequestId.length(), Matchers.greaterThanOrEqualTo(30)),
        () ->
            verify(mockHttpResponse)
                .addHeader(
                    ArgumentMatchers.same(MappedDiagnosticContextKey.REQUEST_ID.getMdcKey()),
                    anyString()),
        () ->
            LoggingVerification.assertLog(
                Level.DEBUG,
                Matchers.startsWith(
                    "Adding the Request Id response header [requestId] with a value of")));
  }

  private String getRequestIdFromTheMdc() {
    return MDC.get(MappedDiagnosticContextKey.REQUEST_ID.getMdcKey());
  }
}
