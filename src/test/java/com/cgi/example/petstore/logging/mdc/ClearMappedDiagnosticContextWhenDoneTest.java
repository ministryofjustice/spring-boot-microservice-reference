package com.cgi.example.petstore.logging.mdc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class ClearMappedDiagnosticContextWhenDoneTest {

  @Mock private HttpServletRequest mockHttpRequest;

  @Mock private HttpServletResponse mockHttpResponse;

  @Mock private FilterChain mockFilterChain;

  private ClearMappedDiagnosticContextWhenDone filter;

  @BeforeEach
  void beforeEach() {
    MDC.clear();
    filter = new ClearMappedDiagnosticContextWhenDone();
  }

  @AfterEach
  void afterEach() {
    MDC.clear();
  }

  @Test
  void should_ClearTheMappedDiagnosticContext() throws ServletException, IOException {
    MappedDiagnosticContextKey.USERNAME.put("alex.stone");
    MappedDiagnosticContextKey.REQUEST_ID.put("216229aa-2af5-40fb-9171-31f6b8d76ca3");

    filter.doFilter(mockHttpRequest, mockHttpResponse, mockFilterChain);

    assertAll(
        () -> verify(mockFilterChain).doFilter(mockHttpRequest, mockHttpResponse),
        () -> assertNull(MDC.get(MappedDiagnosticContextKey.USERNAME.getMdcKey())),
        () -> assertNull(MDC.get(MappedDiagnosticContextKey.REQUEST_ID.getMdcKey())));
  }
}
