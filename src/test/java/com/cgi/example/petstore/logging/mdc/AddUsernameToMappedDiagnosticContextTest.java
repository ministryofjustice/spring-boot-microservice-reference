package com.cgi.example.petstore.logging.mdc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class AddUsernameToMappedDiagnosticContextTest {

  @Mock private HttpServletRequest httpRequest;

  @Mock private HttpServletResponse httpResponse;

  @Mock private Object mockHandler;

  private AddUsernameToMappedDiagnosticContext interceptor;

  @BeforeEach
  void beforeEach() {
    MDC.clear();
    interceptor = new AddUsernameToMappedDiagnosticContext();
  }

  @AfterEach
  void afterEach() {
    MDC.clear();
  }

  @Test
  void should_PopulateMDCWithAuthenticatedUsername() {
    final String expectedUsername = "alex.stone";

    when(httpRequest.getUserPrincipal()).thenReturn(() -> expectedUsername);

    assertNull(getUsernameFromMdc(), "Failed precondition");

    boolean preHandleResult = interceptor.preHandle(httpRequest, httpResponse, mockHandler);

    assertAll(
        () -> assertTrue(preHandleResult),
        () -> assertEquals(expectedUsername, getUsernameFromMdc()));
  }

  private String getUsernameFromMdc() {
    return MDC.get(MappedDiagnosticContextKey.USERNAME.getMdcKey());
  }
}
