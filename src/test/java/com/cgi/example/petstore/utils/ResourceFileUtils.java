package com.cgi.example.petstore.utils;

import com.cgi.example.common.local.ToClickableUriString;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;

@Disabled("Not a test class")
public class ResourceFileUtils {

  private final ToClickableUriString toClickableUriString = new ToClickableUriString();

  public String readFile(String filePath) {
    Path path = Paths.get("src/test/resources", filePath);

    try {
      return Files.readString(path);
    } catch (IOException e) {
      String message =
          "Given the supplied file path [%s] unable to read the file [%s]"
              .formatted(filePath, toClickableUriString.apply(path.toFile()));
      throw new RuntimeException(message, e);
    }
  }
}
