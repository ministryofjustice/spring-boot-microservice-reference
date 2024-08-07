package com.cgi.example.petstore;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Tag;

@AnalyzeClasses(
    packages = "com.cgi.example.petstore",
    importOptions = {
      ImportOption.DoNotIncludeTests.class,
      ImportOption.DoNotIncludeJars.class,
      ImportOption.DoNotIncludeArchives.class
    })
@Tag("unit")
public class ApplicationArchitectureTest {

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_1 =
      noClasses()
          .that()
          .resideOutsideOfPackages(
              "com.cgi.example.petstore.controller",
              "com.cgi.example.petstore.controller.validation")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("com.cgi.example.petstore.controller.validation");

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_2 =
      classes()
          .that()
          .resideInAPackage("com.cgi.example.petstore.logging")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(
              externalPackagesAnd(
                  "com.cgi.example.petstore.logging",
                  "com.cgi.example.petstore.logging.aspects",
                  "com.cgi.example.petstore.logging.mdc"));

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_3 =
      classes()
          .that()
          .resideInAPackage("com.cgi.example.petstore.logging.aspects")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(externalPackagesAnd("com.cgi.example.petstore.logging.aspects"));

  @ArchTest
  public static final ArchRule ARCHITECTURE_TEST_4 =
      classes()
          .that()
          .resideInAPackage("com.cgi.example.petstore.logging.mdc")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(externalPackagesAnd("com.cgi.example.petstore.logging.mdc"));

  private static String[] externalPackagesAnd(String... packages) {
    List<String> externalPackages =
        new LinkedList<>(
            List.of(
                "org.springframework..",
                "java.lang..",
                "java.io..",
                "java.util..",
                "java.security..",
                "org.slf4j..",
                "org.aspectj..",
                "jakarta.servlet.."));

    Collections.addAll(externalPackages, packages);
    return externalPackages.toArray(new String[] {});
  }
}
