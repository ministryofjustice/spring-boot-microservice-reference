package com.cgi.example.loadtest;

import com.cgi.example.loadtest.util.HttpProtocolBuilders;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.status;

@Slf4j
public class ApplicationScenario {

    private static final String ADD_PET_REQUEST_BODY_JSON = """
            {
              "vaccinationId": "AF54785412K",
              "name": "Fido",
              "petType": "Dog",
              "photoUrls": [
                "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
              ],
              "additionalInformation": [
                {
                  "name": "Personality",
                  "description": "Energetic"
                }
              ]
            }
            """;

    private final HttpProtocolBuilders protocolBuilders;
    private final int requestsPerSecond;
    private final Duration timeToRun;

    public ApplicationScenario(int requestsPerSecond, Duration timeToRun) {
        this.protocolBuilders = new HttpProtocolBuilders();
        this.requestsPerSecond = requestsPerSecond;
        this.timeToRun = timeToRun;
    }

    public PopulationBuilder createWith(ConcurrentLinkedQueue<String> oAuth2BearerTokens) {
        HttpRequestActionBuilder addPet = HttpDsl.http("Add Pet")
                .post("/api/v1/pet-store/pets")
                .header("Authorization", "#{oAuth2AuthorizationHeader}")
                .body(StringBody(ADD_PET_REQUEST_BODY_JSON)).asJson()
                .check(status().is(200))
                .check(jsonPath("$.petId").saveAs("petId"));

        HttpRequestActionBuilder getPet = HttpDsl.http("Get Pet")
                .get("/api/v1/pet-store/pets/#{petId}")
                .header("Authorization", "#{oAuth2AuthorizationHeader}")
                .check(status().is(200));

        HttpRequestActionBuilder deletePet = HttpDsl.http("Delete Pet")
                .delete("/api/v1/pet-store/pets/#{petId}")
                .header("Authorization", "#{oAuth2AuthorizationHeader}")
                .check(status().is(200));

        ScenarioBuilder applicationScenario = CoreDsl.scenario("Application scenario")
                .exec(session -> {
                    String oAuth2Header = "Bearer " + oAuth2BearerTokens.remove();
                    log.debug("Retrieved the OAuth2 bearer token from the queue: {}", oAuth2Header);

                    String sessionVariableName = "oAuth2AuthorizationHeader";
                    log.debug("About to set session variable {}: {}", sessionVariableName, oAuth2Header);
                    return session.set(sessionVariableName, oAuth2Header);
                })
                .exec(addPet)
                .exec(getPet)
                .exec(deletePet);

        return applicationScenario.injectOpen(
                        CoreDsl.constantUsersPerSec(requestsPerSecond).during(timeToRun))
                .protocols(protocolBuilders.createApplicationProtocol());
    }
}
