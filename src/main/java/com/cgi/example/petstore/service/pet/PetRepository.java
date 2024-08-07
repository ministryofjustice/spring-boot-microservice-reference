package com.cgi.example.petstore.service.pet;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends MongoRepository<PetDocument, String> {

  List<PetDocument> findByPersistenceStatusAndPetStatusIn(
      String persistenceStatus, Collection<String> statuses);

  Optional<PetDocument> findByPersistenceStatusAndPetId(String persistenceStatus, String petId);
}
