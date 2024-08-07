package com.cgi.example.petstore.logging.mdc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Objects;
import org.springframework.web.servlet.HandlerInterceptor;

public class AddUsernameToMappedDiagnosticContext implements HandlerInterceptor {

  private static final String UNAUTHENTICATED_USER = "UnauthenticatedUser";

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {

    String username = determineUsernameFrom(request);
    MappedDiagnosticContextKey.USERNAME.put(username);

    return true;
  }

  private String determineUsernameFrom(HttpServletRequest httpRequest) {
    Principal userPrincipal = httpRequest.getUserPrincipal();
    if (Objects.nonNull(userPrincipal)) {
      return userPrincipal.getName();
    }

    return UNAUTHENTICATED_USER;
  }
}
