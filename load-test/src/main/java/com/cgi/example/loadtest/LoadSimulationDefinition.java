package com.cgi.example.loadtest;

import com.cgi.example.loadtest.memory.MemoryUsageMetrics;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.Simulation;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class LoadSimulationDefinition extends Simulation {

    private static final Duration TIME_TO_RUN = Duration.ofSeconds(20);
    private static final int REQUESTS_PER_SECOND = 10;

    private final MemoryUsageMetrics memoryUsageMetrics = new MemoryUsageMetrics();

    {
        int requiredNumberOfTokens = TIME_TO_RUN.toSecondsPart() * REQUESTS_PER_SECOND;
        final ConcurrentLinkedQueue<String> oAuth2BearerTokens = new ConcurrentLinkedQueue<>();

        PopulationBuilder retrieveOAuth2Tokens = new RetrieveOAuth2Tokens()
                .storeIn(oAuth2BearerTokens, requiredNumberOfTokens);

        PopulationBuilder memoryUsageScenario = memoryUsageMetrics
                .createMemoryMetricsPopulation(TIME_TO_RUN);

        PopulationBuilder applicationScenario = new ApplicationScenario(REQUESTS_PER_SECOND, TIME_TO_RUN)
                .createWith(oAuth2BearerTokens);

        PopulationBuilder completeScenario = retrieveOAuth2Tokens
                .andThen(memoryUsageScenario, applicationScenario);

        setUp(completeScenario);
    }

    @Override
    public void before() {
        memoryUsageMetrics.beforeCallback();
    }

    @Override
    public void after() {
        memoryUsageMetrics.afterCallback();
    }
}