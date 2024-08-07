package com.cgi.example.common.local;

import com.cgi.example.common.DynamicApplicationFileProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class DynamicApplicationPropertiesRepository {

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER = ThreadLocal.withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        return objectMapper;
    });

    private final ToClickableUriString toClickableUriString = new ToClickableUriString();
    private final Path pathToApplicationProperties;

    public DynamicApplicationPropertiesRepository() {
        pathToApplicationProperties = Path.of(DynamicApplicationFileProperties.FILE_PATH);
    }

    public Integer getWireMockPort() {
        return portNumberOrNull(retrieve().getWireMockPort());
    }

    public Integer getApplicationPort() {
        return portNumberOrNull(retrieve().getApplicationPort());
    }

    public Integer getManagementPort() {
        return portNumberOrNull(retrieve().getManagementPort());
    }

    public String getMongoDBConnectionString() {
        return String.format("mongodb://localhost:%d", portNumberOrNull(retrieve().getMongoDBPort()));
    }

    public Integer getOAuth2Port() {
        return portNumberOrNull(retrieve().getOAuth2Port());
    }

    public String getOAuth2Host() {
        return String.format("http://localhost:%d", getOAuth2Port());
    }

    public void setApplicationPort(Class<?> modifiedBy, int applicationPortNumber) {
        Port port = createPort(modifiedBy, applicationPortNumber);
        save(dynamicApplicationProperties -> dynamicApplicationProperties.setApplicationPort(port));
    }

    public void setManagementPort(Class<?> modifiedBy, int managementPortNumber) {
        Port port = createPort(modifiedBy, managementPortNumber);
        save(dynamicApplicationProperties -> dynamicApplicationProperties.setManagementPort(port));
    }

    public void setWireMockPort(Class<?> modifiedBy, int wireMockPort) {
        Port port = createPort(modifiedBy, wireMockPort);
        save(dynamicApplicationProperties -> dynamicApplicationProperties.setWireMockPort(port));
    }

    public void setMongoDBPort(Class<?> modifiedBy, int mongoDBPort) {
        Port port = createPort(modifiedBy, mongoDBPort);
        save(dynamicApplicationProperties -> dynamicApplicationProperties.setMongoDBPort(port));
    }

    public void setOAuth2Port(Class<?> modifiedBy, int oAuth2Port) {
        Port port = createPort(modifiedBy, oAuth2Port);
        save(dynamicApplicationProperties -> dynamicApplicationProperties.setOAuth2Port(port));
    }

    private Integer portNumberOrNull(Port port) {
        return Objects.isNull(port) ? null : port.getPort();
    }

    @Nonnull
    private synchronized DynamicApplicationProperties retrieve() {
        File file = pathToApplicationProperties.toFile();
        if (!file.exists()) {
            log.debug("Given the the file path [{}] unable to find: {}", file.getAbsolutePath(), toClickableUriString.apply(file));
            return new DynamicApplicationProperties();
        }

        return readApplicationPropertiesFrom(pathToApplicationProperties);
    }

    private synchronized void save(Consumer<DynamicApplicationProperties> changesToApply) {
        try {
            DynamicApplicationProperties applicationProperties = retrieve();
            changesToApply.accept(applicationProperties);

            String dynamicApplicationPropertiesJson =
                    OBJECT_MAPPER.get().writeValueAsString(applicationProperties);
            log.warn("About to save dynamicApplicationProperties JSON: [{}] to: {}",
                    dynamicApplicationPropertiesJson, toClickableUriString.apply(pathToApplicationProperties.toFile()));

            Files.createDirectories(pathToApplicationProperties.getParent());
            Files.writeString(
                    pathToApplicationProperties,
                    dynamicApplicationPropertiesJson,
                    StandardCharsets.UTF_8);
            log.info("Successfully saved dynamicApplicationProperties: {}", toClickableUriString.apply(pathToApplicationProperties.toFile()));
        } catch (JsonProcessingException e) {
            log.warn("Unable to deserialise dynamicApplicationProperties: {}", e.getMessage(), e);
        } catch (IOException e) {
            log.warn("Unable to save dynamicApplicationProperties to disk: {}", e.getMessage(), e);
        }
    }

    @Nonnull
    private DynamicApplicationProperties readApplicationPropertiesFrom(Path applicationPropertiesPath) {
        try {
            String dynamicApplicationPropertiesJson = Files.readString(applicationPropertiesPath);
            log.debug("Retrieved dynamicApplicationProperties: {}", dynamicApplicationPropertiesJson);

            return OBJECT_MAPPER.get().readValue(dynamicApplicationPropertiesJson, DynamicApplicationProperties.class);
        } catch (IOException e) {
            log.info("Unable to retrieve dynamicApplicationProperties: {}", e.getMessage(), e);
        }
        return new DynamicApplicationProperties();
    }

    private Port createPort(Class<?> modifiedBy, int portNumber) {
        return Port.builder()
                .port(portNumber)
                .modifiedBy(modifiedBy.getCanonicalName())
                .modifiedAt(Instant.now())
                .build();
    }
}
