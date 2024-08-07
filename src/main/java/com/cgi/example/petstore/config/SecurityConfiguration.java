package com.cgi.example.petstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain httpSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            http -> {
              http.requestMatchers(HttpMethod.GET, "/swagger-ui/**")
                  .permitAll()
                  .requestMatchers(HttpMethod.GET, "/v3/api-docs/**")
                  .permitAll()
                  .requestMatchers(HttpMethod.GET, "/actuator/**")
                  .permitAll()
                  .requestMatchers(HttpMethod.GET, "/v3/api-docs.yaml")
                  .permitAll()
                  .requestMatchers(HttpMethod.GET, "/swagger-ui.html")
                  .permitAll()
                  .requestMatchers("/api/v1/pet-store/pets/**")
                  .authenticated()
                  .anyRequest()
                  .denyAll();
            })
        .oauth2ResourceServer(oauth2())
        .sessionManagement(sessionManagement())
        .build();
  }

  private Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oauth2() {
    BearerTokenAuthenticationEntryPoint entryPoint = new BearerTokenAuthenticationEntryPoint();
    entryPoint.setRealmName("Pet Store API Realm");

    return oauth2 ->
        oauth2
            .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            .authenticationEntryPoint(entryPoint)
            .jwt(Customizer.withDefaults());
  }

  private Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagement() {
    return sessionSecurity ->
        sessionSecurity.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }
}
