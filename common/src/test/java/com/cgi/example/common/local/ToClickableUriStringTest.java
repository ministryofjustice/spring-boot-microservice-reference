package com.cgi.example.common.local;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class ToClickableUriStringTest {

    @TempDir
    private Path temporaryDirectory;

    private ToClickableUriString toClickableUriString;

    @BeforeEach
    void setUp() {
        toClickableUriString = new ToClickableUriString();
    }

    @Test
    void shouldReturnAClickableUriWhenTheFileDoesNotExist() {
        String fileName = "doesNotExist.json";
        Path pathToFileWhichDoesNotExist = Paths.get(temporaryDirectory.toFile().getAbsolutePath(), fileName);

        File nonExistentFile = pathToFileWhichDoesNotExist.toFile();
        assertFalse(nonExistentFile.exists(), "Failed precondition");

        String clickableUri = toClickableUriString.apply(nonExistentFile);

        assertThat(clickableUri, Matchers.startsWith("file:///"));
        assertThat(clickableUri, Matchers.containsString(fileName));
    }

    @Test
    void shouldReturnAClickableUriWhenTheFileExists() throws IOException {
        String fileName = "report.json";
        File file = new File(temporaryDirectory.toFile(), fileName);

        assertTrue(file.createNewFile(), "Failed precondition");
        assertTrue(file.exists(), "Failed precondition");

        String clickableUri = toClickableUriString.apply(file);

        assertThat(clickableUri, Matchers.startsWith("file:///"));
        assertThat(clickableUri, Matchers.containsString(fileName));
    }
}