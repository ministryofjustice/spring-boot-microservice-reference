package com.cgi.example.loadtest.memory;

import com.cgi.example.loadtest.util.HttpProtocolBuilders;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

@Slf4j
public class MemoryUsageMetrics {

    private static final int RECORD_ONCE_PER_SECOND = 1;

    private final ConcurrentLinkedQueue<MemoryUsageMetric> memoryUsageMetricsQueue = new ConcurrentLinkedQueue<>();
    private final HttpProtocolBuilders protocolBuilders = new HttpProtocolBuilders();

    public void beforeCallback() {
        memoryUsageMetricsQueue.clear();
    }

    public void afterCallback() {
        log.info("Collected {} metrics data points", memoryUsageMetricsQueue.size());
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        memoryUsageMetricsQueue.stream()
                .map(memoryUsageMetric -> memoryUsageMetric.getMemoryUsedInBytes().doubleValue())
                .forEach(descriptiveStatistics::addValue);

        double minimumMemoryUsage = descriptiveStatistics.getMin();
        double maximumMemoryUsage = descriptiveStatistics.getMax();
        log.info("Memory usage {}: {}", "min", toMegaBytes(minimumMemoryUsage));
        log.info("Memory usage {}: {}", "max", toMegaBytes(maximumMemoryUsage));
        log.info("Memory usage {}: {}", "mean", toMegaBytes(descriptiveStatistics.getMean()));

        if (maximumMemoryUsage > 10.0 * minimumMemoryUsage) {
            String message = "Maximum memory usage %s is over 10 times the minimum memory usage %s"
                    .formatted(maximumMemoryUsage, minimumMemoryUsage);
            throw new IllegalStateException(message);
        }
    }

    public PopulationBuilder createMemoryMetricsPopulation(Duration timeToRun) {
        ScenarioBuilder getMemoryUsageScenario = CoreDsl.scenario("Memory Usage scenario")
                .exec(retrieveMemoryUsage())
                .exec(recordMemoryUsage());

        HttpProtocolBuilder managementProtocol = protocolBuilders.createManagementProtocol();
        return getMemoryUsageScenario.injectOpen(
                        CoreDsl.constantUsersPerSec(RECORD_ONCE_PER_SECOND)
                                .during(timeToRun))
                .protocols(managementProtocol);
    }

    private static HttpRequestActionBuilder retrieveMemoryUsage() {
        return HttpDsl.http("Get Memory Usage")
                .get("/actuator/metrics/jvm.memory.used")
                .check(HttpDsl.status().is(HttpResponseStatus.OK.code()))
                .check(CoreDsl.jsonPath("$.name").is("jvm.memory.used"))
                .check(CoreDsl.jsonPath("$.baseUnit").is("bytes"))
                .check(CoreDsl.jsonPath("$.measurements[?(@.statistic == 'VALUE')].value").saveAs("memoryUsedInBytes"));
    }

    private Function<Session, Session> recordMemoryUsage() {
        return session -> {
            String memoryUsedInBytes = session.getString("memoryUsedInBytes");
            MemoryUsageMetric memoryUsageMetric = new MemoryUsageMetric(memoryUsedInBytes);
            log.debug("Recorded memoryUsageMetric: [{}]", memoryUsageMetric);
            memoryUsageMetricsQueue.add(memoryUsageMetric);
            return session;
        };
    }

    private String toMegaBytes(double amountInBytes) {
        double amountInMegaBytes = amountInBytes / (1024.0 * 1024.0);
        DecimalFormat withThousandSeparators = new DecimalFormat("#,##0");

        return withThousandSeparators.format(amountInMegaBytes) + "MB";
    }
}
