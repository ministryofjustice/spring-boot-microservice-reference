package com.cgi.example.petstore.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.function.Executable;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Disabled("Not a test class")
public class AssertionExecutables {

  public Executable assertContentType(HttpEntity<?> response, String expectedContentType) {
    return () -> {
      List<String> contentTypes = response.getHeaders().get(HttpHeaders.CONTENT_TYPE);

      assertThat(contentTypes, Matchers.equalTo(List.of(expectedContentType)));
    };
  }

  public Executable assertContains(String actual, String expectedString) {
    return () -> assertThat(actual, Matchers.containsString(expectedString));
  }

  public Executable assertJsonContentType(HttpEntity<?> response) {
    return assertContentType(response, MediaType.APPLICATION_JSON_VALUE);
  }

  public Executable assertProblemJsonContentType(HttpEntity<?> response) {
    return assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE);
  }

  public Executable assertOkJsonResponse(ResponseEntity<?> response) {
    return () -> {
      assertStatusCodeCommon(response, HttpStatus.OK);
      assertJsonContentType(response).execute();
    };
  }

  public Executable assertJsonPathEquals(Object expectedValue, String jsonPath, String actualJson) {
    return () -> {
      Object actualJsonPathValue = JsonPath.read(actualJson, jsonPath);
      assertEquals(expectedValue, actualJsonPathValue);
    };
  }

  public Executable assertLenientJsonEquals(String expectedJson, String actualJson) {
    return () -> JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
  }

  public Executable assertStatusCode(ResponseEntity<?> response, HttpStatus httpStatusCode) {
    return () -> assertStatusCodeCommon(response, httpStatusCode);
  }

  private void assertStatusCodeCommon(ResponseEntity<?> response, HttpStatusCode httpStatusCode) {
    assertEquals(httpStatusCode, response.getStatusCode());
  }
}
