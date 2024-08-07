package com.cgi.example.petstore.service.pet;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document(collection = "pets")
public class PetDocument {

  @Id
  @Indexed(unique = true, name = "petIdIndex")
  private String petId;

  @Indexed(name = "ownerCustomerIdIndex")
  private String ownerCustomerId;

  private String vaccinationId;

  @Indexed(name = "petNameIndex")
  private String name;

  private String petType;

  private List<String> photoUrls;

  private List<PetInformationItemPersistenceType> additionalInformation;

  @Indexed(name = "petStatusIndex")
  private String petStatus;

  private String persistenceStatus;

  @CreatedDate private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime lastModified;
}
