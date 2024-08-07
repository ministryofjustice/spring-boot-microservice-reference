package com.cgi.example.petstore.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the target/source class for which the logs are to be recorded such that test
 * assertions can be made.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestLoggingTarget {

  Class<?> value();
}
