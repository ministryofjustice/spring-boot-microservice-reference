package com.cgi.example.petstore.service.pet;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PetInformationItemPersistenceType {

  private String name;

  private String description;
}
