package com.cgi.example.apitest;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.common.local.ToClickableUriString;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ApiTestApplication {

    private static final String POSTMAN_COLLECTION = "src/main/resources/spring-boot-microservice-template.postman_collection.json";
    private static final String POSTMAN_ENVIRONMENT = "src/main/resources/local.postman_environment.json";

    private final DynamicApplicationPropertiesRepository propertiesRepository = new DynamicApplicationPropertiesRepository();
    private final ToClickableUriString toClickableUriString = new ToClickableUriString();

    /**
     * To run this main method directly, your working directory must be set to be spring-boot-microservice-template\api-test
     * If using IntelliJ this is found under "Run/Debug Configurations".
     *
     * @param args Not used
     */
    public static void main(String[] args) {
        ApiTestApplication apiTestApplication = new ApiTestApplication();
        apiTestApplication.start();
    }

    private void start() {
        File htmlReport = reportFile("html");
        File jsonReport = reportFile("json");

        ProcessBuilder processBuilder = createProcessBuilder(htmlReport, jsonReport);

        Process process = startProcess(processBuilder);

        int exitCode = waitForProcessToComplete(process);

        log.info("API tests completed with exit code: {}", exitCode);
        log.info("HTML test report has been saved to: {}", toClickableUriString.apply(htmlReport));
        log.info("JSON test report has been saved to: {}", toClickableUriString.apply(jsonReport));
        if (exitCode != 0) {
            System.err.println("API test failure, see reports above");
        }
        System.exit(exitCode);
    }

    private ProcessBuilder createProcessBuilder(File htmlReport, File jsonReport) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);

        processBuilder.command("cmd", "/c", "newman",
                "run", POSTMAN_COLLECTION,
                "--reporters", "cli,htmlextra,json",
                "--reporter-htmlextra-export", htmlReport.getAbsolutePath(),
                "--reporter-json-export", jsonReport.getAbsolutePath(),
                "--environment", POSTMAN_ENVIRONMENT,
                "--env-var", "applicationPort=" + propertiesRepository.getApplicationPort(),
                "--env-var", "managementPort=" + propertiesRepository.getManagementPort(),
                "--env-var", "wireMockPort=" + propertiesRepository.getWireMockPort(),
                "--env-var", "oAuth2Port=" + propertiesRepository.getOAuth2Port());

        return processBuilder;
    }


    private File reportFile(String fileExtension) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String creationTime = dateTimeFormatter.format(LocalDateTime.now());

        String relativePath = ("build/newman/api-test-report-%s.%s").formatted(creationTime, fileExtension);

        return Paths.get(relativePath).toFile();
    }

    private int waitForProcessToComplete(Process process) {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            log.error("InterruptedException thrown when executing API tests: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Process startProcess(ProcessBuilder processBuilder) {
        try {
            log.info("Starting API tests");
            Process started = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(started.getInputStream()))) {
                reader.lines()
                        .forEach(log::info);
            }

            return started;
        } catch (IOException e) {
            log.error("IOException thrown when executing API tests: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
