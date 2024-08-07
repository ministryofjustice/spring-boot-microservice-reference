package com.cgi.example.loadtest;

import com.cgi.example.loadtest.util.HttpProtocolBuilders;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.status;

@Slf4j
public class RetrieveOAuth2Tokens {
    /**
     * Extract the OAuth2 Code from a String of the form /?code=mqJ1NWjDOVe87-bNXz3vPAvGBg3cP3QvzXOHMl7iFDg
     */
    private static final Function<String, String> TO_OAUTH2_CODE = locationValue -> {
        String codeMarker = "code=";
        int indexOfCodeMarker = locationValue.indexOf(codeMarker);
        String oAuth2Code = locationValue.substring(indexOfCodeMarker + codeMarker.length());
        log.debug("Extracted oAuth2Code: {}", oAuth2Code);
        return oAuth2Code;
    };

    private final HttpProtocolBuilders protocolBuilders = new HttpProtocolBuilders();

    /**
     * Note: This method is not thread safe!
     */
    public PopulationBuilder storeIn(ConcurrentLinkedQueue<String> oauth2BearerTokens, long requiredNumberOfTokens) {
        long timeToRun = BigDecimal.valueOf(requiredNumberOfTokens)
                .divide(BigDecimal.TEN, RoundingMode.UP)
                .longValueExact();

        return buildOAuth2(oauth2BearerTokens)
                .injectOpen(CoreDsl.constantUsersPerSec(10).during(timeToRun))
                .protocols(protocolBuilders.createOAuth2Protocol());
    }

    private ScenarioBuilder buildOAuth2(ConcurrentLinkedQueue<String> oauth2BearerTokens) {
        HttpRequestActionBuilder oAuth2Authorize = HttpDsl.http("OAuth2 authorize")
                .get("/default/authorize?response_type=code&client_id=SomeClientId&scope=openid&redirect_uri=%2F")
                .disableFollowRedirect()
                .check(status().is(302))
                .check(header("location").exists())
                .check(header("location").transform(TO_OAUTH2_CODE).saveAs("oAuth2Code"))
                .check();

        HttpRequestActionBuilder oAuth2Token = HttpDsl.http("OAuth2 get and store token")
                .post("/default/token")
                .body(StringBody("grant_type=authorization_code&code=#{oAuth2Code}&redirect_uri=/&client_id=someClientId")).asFormUrlEncoded()
                .check(status().is(200))
                .check(jsonPath("$.access_token").exists())
                .check(jsonPath("$.access_token").validate("Store OAuth2 token", (accessToken, session) -> {
                    log.debug("Storing the OAuth2 access token: {}", accessToken);
                    oauth2BearerTokens.add(accessToken);
                    return accessToken;
                }));

        return CoreDsl.scenario("OAuth2 scenario")
                .exec(oAuth2Authorize)
                .exec(oAuth2Token);
    }
}
