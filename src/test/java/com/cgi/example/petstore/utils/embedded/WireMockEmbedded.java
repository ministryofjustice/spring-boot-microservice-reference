package com.cgi.example.petstore.utils.embedded;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.petstore.utils.ProcessManagement;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.trafficlistener.ConsoleNotifyingWiremockNetworkTrafficListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;

@Slf4j
@Disabled("Not a test class")
public class WireMockEmbedded {

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();

  @Getter private final WireMockServer wireMockServer;

  /**
   * Used to start the Wire Mock Server independently of integration tests. e.g. If running the
   * microservice locally you may want to stub external API calls using Wire Mock.
   */
  public static void main(String[] args) {
    new WireMockEmbedded();
  }

  public WireMockEmbedded() {
    WireMockConfiguration wireMockConfiguration =
        WireMockConfiguration.options()
            .dynamicPort()
            .usingFilesUnderClasspath("src\\test\\resources\\wiremock")
            .globalTemplating(true)
            .notifier(new ConsoleNotifier("WireMockConsoleLog", true))
            .maxRequestJournalEntries(100)
            .networkTrafficListener(new ConsoleNotifyingWiremockNetworkTrafficListener());
    wireMockServer = new WireMockServer(wireMockConfiguration);
    start();
  }

  private void start() {
    if (isRunning()) {
      log.debug("Cannot start Embedded WireMock as it is already running");
      return;
    }

    log.info("Starting Wire Mock Server");
    wireMockServer.start();
    ProcessManagement.waitUntil(wireMockServer::isRunning);

    int wireMockPort = wireMockServer.port();
    log.info("Started Wire Mock Server on port: {}", wireMockPort);

    propertiesRepository.setWireMockPort(getClass(), wireMockPort);
  }

  public boolean isRunning() {
    return wireMockServer.isRunning();
  }

  public void resetAll() {
    wireMockServer.resetAll();
  }
}
