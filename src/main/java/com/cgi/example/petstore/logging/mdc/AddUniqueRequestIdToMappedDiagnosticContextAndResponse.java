package com.cgi.example.petstore.logging.mdc;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddUniqueRequestIdToMappedDiagnosticContextAndResponse implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    final String uniqueRequestId = UUID.randomUUID().toString();
    MappedDiagnosticContextKey.REQUEST_ID.put(uniqueRequestId);

    chain.doFilter(request, response);

    if (response instanceof HttpServletResponse httpServletResponse) {
      String requestIdHeaderName = MappedDiagnosticContextKey.REQUEST_ID.getMdcKey();
      log.debug(
          "Adding the Request Id response header [{}] with a value of [{}]",
          requestIdHeaderName,
          uniqueRequestId);
      httpServletResponse.addHeader(requestIdHeaderName, uniqueRequestId);
    }
  }
}
