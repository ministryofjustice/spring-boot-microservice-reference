package com.cgi.example.petstore.local;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
public class RecordApplicationPortNumbersOnStartup
    implements ApplicationListener<WebServerInitializedEvent> {

  private static final String APPLICATION_MANAGEMENT = "application:management";
  private static final String LOCAL_SERVER_PORT = "local.server.port";

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();

  @Override
  public void onApplicationEvent(WebServerInitializedEvent event) {
    WebServerApplicationContext applicationContext = event.getApplicationContext();

    if (APPLICATION_MANAGEMENT.equalsIgnoreCase(applicationContext.getId())) {
      int applicationPortNumber = applicationPortNumber(applicationContext);
      int managementPortNumber = managementPortNumber(event);

      propertiesRepository.setApplicationPort(getClass(), applicationPortNumber);
      propertiesRepository.setManagementPort(getClass(), managementPortNumber);
    }
  }

  private int applicationPortNumber(WebServerApplicationContext applicationContext) {
    Integer portNumber =
        applicationContext.getEnvironment().getProperty(LOCAL_SERVER_PORT, Integer.class);

    if (Objects.isNull(portNumber) || portNumber <= 0) {
      log.warn(
          "Could not determine the Application Port Number, expected an integer greater than 0 but found: [{}] so defaulting to 0",
          portNumber);
      portNumber = 0;
    }

    return portNumber;
  }

  private int managementPortNumber(WebServerInitializedEvent event) {
    return event.getWebServer().getPort();
  }
}
