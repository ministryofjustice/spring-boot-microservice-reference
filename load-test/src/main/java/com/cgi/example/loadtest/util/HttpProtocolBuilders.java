package com.cgi.example.loadtest.util;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class HttpProtocolBuilders {

    private static final String APPLICATION_JSON = "application/json";
    private static final String HOSTNAME = "localhost";

    private final DynamicApplicationPropertiesRepository propertiesRepository = new DynamicApplicationPropertiesRepository();

    public HttpProtocolBuilder createApplicationProtocol() {
        Integer applicationPort = propertiesRepository.getApplicationPort();
        return createProtocolOrElseThrowException(applicationPort,
                "Unable to determine the application port from DynamicApplicationProperties, is the microservice running?");
    }

    public HttpProtocolBuilder createManagementProtocol() {
        Integer managementPort = propertiesRepository.getManagementPort();
        return createProtocolOrElseThrowException(managementPort,
                "Unable to determine the management port from DynamicApplicationProperties, is the microservice running?");
    }

    public HttpProtocolBuilder createOAuth2Protocol() {
        Integer oAuth2Port = propertiesRepository.getOAuth2Port();
        return createProtocolOrElseThrowException(oAuth2Port,
                "Unable to determine the OAuth2 port from DynamicApplicationProperties, is the microservice running?");
    }

    private HttpProtocolBuilder createProtocolOrElseThrowException(Integer managementPort, String exceptionMessage) {
        if (Objects.isNull(managementPort)) {
            throw new IllegalStateException(exceptionMessage);
        }

        return createHttpProtocol(managementPort);
    }

    private HttpProtocolBuilder createHttpProtocol(int portNumber) {
        String baseUrl = "http://" + HOSTNAME + ":" + portNumber;
        return HttpDsl.http.baseUrl(baseUrl)
                .acceptHeader(APPLICATION_JSON)
                .disableCaching()
                .disableUrlEncoding()
                .contentTypeHeader(APPLICATION_JSON);
    }
}
