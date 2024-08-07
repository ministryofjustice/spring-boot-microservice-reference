package com.cgi.example.loadtest;

import io.gatling.app.Gatling;
import io.gatling.shared.cli.CliOption;
import io.gatling.shared.cli.GatlingCliOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadTestApplication {

    public static void main(String[] args) {
        LoadTestApplication loadTestApplication = new LoadTestApplication();
        loadTestApplication.run();
    }

    private void run() {
        String canonicalName = LoadSimulationDefinition.class.getCanonicalName();

        String[] gatlingArgs = {config(GatlingCliOptions.Simulation, canonicalName),
                config(GatlingCliOptions.ResultsFolder, "build/load-test-results"),
                config(GatlingCliOptions.RunDescription, "Pet Store load testing")};

        Gatling.main(gatlingArgs);
    }

    private String config(CliOption option, String value) {
        return "--" + option.longName + "=" + value;
    }
}
