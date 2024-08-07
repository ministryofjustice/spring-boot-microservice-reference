package com.cgi.example.petstore.utils.embedded;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.OAuth2Config;
import org.junit.jupiter.api.Disabled;

@Slf4j
@Disabled("Not a test class")
public class OAuth2Embedded {

  private static final boolean NO_INTERACTIVE_LOGIN = false;

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();

  private final MockOAuth2Server mockOAuth2Server;

  public static void main(String[] args) {
    new OAuth2Embedded();
  }

  public OAuth2Embedded() {
    OAuth2Config config = new OAuth2Config(NO_INTERACTIVE_LOGIN);
    mockOAuth2Server = new MockOAuth2Server(config);

    log.info("Starting OAuth2 Embedded");
    mockOAuth2Server.start();
    int port = mockOAuth2Server.getConfig().getHttpServer().port();
    log.info("Started OAuth2 Embedded on port: {}", port);

    propertiesRepository.setOAuth2Port(getClass(), port);
  }

  public String issueToken() {
    String issuerId = propertiesRepository.getOAuth2Host() + "/default";

    return mockOAuth2Server.issueToken(issuerId, "DummyClientId", "petStoreAPI").serialize();
  }
}
