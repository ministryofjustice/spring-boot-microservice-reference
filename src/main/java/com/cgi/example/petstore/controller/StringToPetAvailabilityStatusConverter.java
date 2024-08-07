package com.cgi.example.petstore.controller;

import com.cgi.example.petstore.model.PetAvailabilityStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPetAvailabilityStatusConverter
    implements Converter<String, PetAvailabilityStatus> {

  @Override
  public PetAvailabilityStatus convert(String source) {
    return PetAvailabilityStatus.fromValue(source);
  }
}
