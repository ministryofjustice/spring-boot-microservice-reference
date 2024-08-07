package com.cgi.example.petstore.utils;

import com.cgi.example.petstore.integration.BaseIntegrationTest;
import java.net.URI;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Disabled("Not a test class")
public class WebClientExecutor {

  private final WebClient webClient;

  public ResponseEntity<String> get(UriComponentsBuilder uriBuilder) {
    WebClient.RequestHeadersSpec<?> request = webClient.get().uri(toURI(uriBuilder));

    return execute(request);
  }

  public ResponseEntity<String> patch(UriComponentsBuilder uriBuilder, Object body) {
    WebClient.RequestHeadersSpec<?> request =
        webClient.patch().uri(toURI(uriBuilder)).bodyValue(body);

    return execute(request);
  }

  public ResponseEntity<String> delete(UriComponentsBuilder uriBuilder) {
    WebClient.RequestHeadersSpec<?> request = webClient.delete().uri(toURI(uriBuilder));

    return execute(request);
  }

  public ResponseEntity<String> post(UriComponentsBuilder uriBuilder, Object body) {
    WebClient.RequestHeadersSpec<?> request =
        webClient.post().uri(toURI(uriBuilder)).bodyValue(body);

    return execute(request);
  }

  @NotNull private URI toURI(UriComponentsBuilder uriBuilder) {
    return uriBuilder.build().toUri();
  }

  private ResponseEntity<String> execute(WebClient.RequestHeadersSpec<?> request) {

    WebClient.RequestHeadersSpec<?> requestWithOAuthHeader =
        request.header("Authorization", BaseIntegrationTest.getOAuth2AuthorizationHeader());

    log.info("Request: [{}]", requestWithOAuthHeader);

    ResponseEntity<String> response =
        requestWithOAuthHeader
            .exchangeToMono(clientResponse -> clientResponse.toEntity(String.class))
            .onErrorResume(onError())
            .block();

    log.info("Response: [{}]", response);
    return response;
  }

  private Function<Throwable, Mono<? extends ResponseEntity<String>>> onError() {
    return throwable -> {
      if (throwable instanceof WebClientResponseException exception) {
        ResponseEntity<String> responseEntity =
            ResponseEntity.status(exception.getStatusCode())
                .headers(exception.getHeaders())
                .body(exception.getResponseBodyAsString());
        return Mono.just(responseEntity);
      }
      log.error("Unable to create HTTP ResponseEntity: {}", throwable.getMessage(), throwable);
      throw new IllegalStateException("Unable to create HTTP ResponseEntity", throwable);
    };
  }
}
