package com.cgi.example.petstore.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetResponse;
import com.cgi.example.petstore.service.pet.PetDataStoreService;
import com.cgi.example.petstore.service.pet.PetDocument;
import com.cgi.example.petstore.service.pet.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("integration")
public class PetDataStoreServiceIntegrationTest extends BaseIntegrationTest {

  private final TestData testData = new TestData();

  @Autowired private PetDataStoreService petDataStoreService;

  @Autowired private PetRepository petRepository;

  @Test
  void should_SavePetToMongoDB() {
    NewPetRequest petToSave = testData.createNewPetRequest();

    assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

    PetResponse insertedPet = petDataStoreService.insertNewPet(petToSave);

    List<PetDocument> actualAllPetDocuments = petRepository.findAll();
    assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
    PetDocument actualPetDocument = actualAllPetDocuments.getFirst();
    assertEquals(insertedPet.getPetId(), actualPetDocument.getPetId());
  }
}
