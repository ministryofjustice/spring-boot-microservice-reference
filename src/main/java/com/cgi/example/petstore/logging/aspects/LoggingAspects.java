package com.cgi.example.petstore.logging.aspects;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspects {

  @Before("@annotation(com.cgi.example.petstore.logging.aspects.LogMethodArguments)")
  public void logMethodArgumentsAdvice(JoinPoint joinPoint) {
    if (log.isInfoEnabled()) {
      Object[] argumentsArray = Objects.requireNonNullElse(joinPoint.getArgs(), new Object[] {});
      String allMethodArguments =
          Arrays.stream(argumentsArray)
              .map(Object::toString)
              .collect(Collectors.joining(",", "[", "]"));

      String classAndMethodNames = getClassAndMethodName(joinPoint);

      log.info("{} methodArguments: {}", classAndMethodNames, allMethodArguments);
    }
  }

  @AfterReturning(
      value = "@annotation(com.cgi.example.petstore.logging.aspects.LogMethodResponse)",
      returning = "methodResponse")
  public void logMethodResponseAdvice(JoinPoint joinPoint, Object methodResponse) {
    if (log.isInfoEnabled()) {
      String classAndMethodName = getClassAndMethodName(joinPoint);

      log.info("{} methodResponse: {}", classAndMethodName, methodResponse);
    }
  }

  private String getClassAndMethodName(JoinPoint joinPoint) {
    return joinPoint.getSignature().toShortString();
  }
}
