package com.cgi.example.petstore.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetInformationItem;
import com.cgi.example.petstore.model.PetPatchRequest;
import com.cgi.example.petstore.service.pet.PersistenceStatus;
import com.cgi.example.petstore.service.pet.PetDocument;
import com.cgi.example.petstore.service.pet.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import com.cgi.example.petstore.utils.UriBuilder;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Tag("integration")
class ApplicationIntegrationTest extends BaseIntegrationTest {

  private final TestData testData = new TestData();

  @Autowired private PetRepository petRepository;

  @Test
  void should_SuccessfullyAddPet() {
    NewPetRequest petToAdd = testData.createNewPetRequest();

    assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

    UriComponentsBuilder uri = uriBuilder.getPetStoreBaseURI();
    ResponseEntity<String> response = webClientExecutor.post(uri, petToAdd);

    String expectedJsonBody =
        """
                                      {
                        "availabilityStatus": "Available For Purchase",
                                        "vaccinationId": "AF54785412K",
                                        "name": "Fido",
                                        "petType": "Dog",
                                        "photoUrls": [
                                          "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                                        ],
                                        "additionalInformation": [
                                          {
                                            "name": "Personality",
                                            "description": "Energetic"
                                          }
                                        ]
                                      }
                              """;

    String actualJsonBody = response.getBody();
    String actualGeneratedPetId = JsonPath.read(actualJsonBody, "$.petId");

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        () -> assertThat(actualGeneratedPetId, not(isEmptyOrNullString())));

    List<PetDocument> actualAllPetDocuments = petRepository.findAll();
    assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
    PetDocument actualPetDocument = actualAllPetDocuments.getFirst();
    assertEquals(actualGeneratedPetId, actualPetDocument.getPetId());
  }

  @Test
  void should_ReturnFido_When_CallingGetPetEndpoint() {
    PetDocument petDocument = testData.createPetDocument();
    String petId = petDocument.getPetId();
    petRepository.save(petDocument);

    assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(petId);
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String expectedJsonBody =
        """
                                      {
                                        "petId": "KT1546",
                                        "vaccinationId": "AF54785412K",
                                        "name": "Fido",
                                        "petType": "Dog",
                        "availabilityStatus": "Available For Purchase"
                                      }
                                """;

    String actualJsonBody = response.getBody();

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody));
  }

  @Test
  void should_NotReturnPet_When_CallingGetPetEndpointForArchivedPet() {
    PetDocument petDocument = testData.createPetDocument();
    petDocument.setPersistenceStatus(PersistenceStatus.ARCHIVED.getValue());
    String petId = petDocument.getPetId();

    petRepository.save(petDocument);

    assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(petId);
    ResponseEntity<String> response = webClientExecutor.get(uri);

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.NOT_FOUND),
        assertions.assertProblemJsonContentType(response));
  }

  @Test
  void should_ReturnNotFound_When_CallingGetPetWithUnknownPetId() {
    assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor("13");
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String expectedJsonBody =
        """
                             {
                               "type": "about:blank",
                               "title": "Not Found",
                               "status": 404,
                               "detail": "Handled by GlobalExceptionHandler: [Unable to find the pet with Id: [13]]",
                               "instance": "/api/v1/pet-store/pets/13"
                             }
                        """;

    String actualJsonBody = response.getBody();

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.NOT_FOUND),
        assertions.assertProblemJsonContentType(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody));
  }

  @Test
  void should_ReturnError_When_CallingGetPetEndpointWithIdLargerThanPermitted() {
    assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

    String longPetId = "abcdefghijklmnopqrstuvwxyz0123456789";
    assertThat("Failed precondition", longPetId.length(), Matchers.greaterThan(26));

    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(longPetId);
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String responseBody = response.getBody();
    String instance = JsonPath.read(responseBody, "$.instance");

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.BAD_REQUEST),
        assertions.assertProblemJsonContentType(response),
        assertions.assertJsonPathEquals(HttpStatus.BAD_REQUEST.value(), "$.status", responseBody),
        assertions.assertJsonPathEquals(
            "Handled by GlobalExceptionHandler: [getPetById.petId: size must be between 0 and 26]",
            "$.detail",
            responseBody),
        () ->
            assertThat(
                instance,
                CoreMatchers.containsString(UriBuilder.PET_STORE_BASE_URL + "/" + longPetId)));
  }

  @Test
  void should_ReturnError_When_CallingGetPetEndpointWithInvalidIdFailingValidation() {
    assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor("666");
    ResponseEntity<String> response = webClientExecutor.get(uri);

    int status = JsonPath.read(response.getBody(), "$.status");
    String instance = JsonPath.read(response.getBody(), "$.instance");
    String detail = JsonPath.read(response.getBody(), "$.detail");

    assertAll(
        assertions.assertStatusCode(response, HttpStatus.BAD_REQUEST),
        assertions.assertProblemJsonContentType(response),
        () -> assertEquals(HttpStatus.BAD_REQUEST.value(), status),
        () ->
            assertEquals(
                "Handled by GlobalExceptionHandler: [Invalid Pet Id, the Id [666] is not permitted, found: [666]]",
                detail),
        () ->
            assertThat(
                instance, CoreMatchers.containsString(UriBuilder.PET_STORE_BASE_URL + "/666")));
  }

  @Test
  void should_ReturnPetsWithMatchingStatuses_When_CallingFindByStatus() {
    PetDocument petDocumentLassie =
        createPetDocument("KT1546", "Lassie", PetAvailabilityStatus.PENDING_COLLECTION);
    PetDocument petDocumentAstro = createPetDocument("ABC456", "Astro", PetAvailabilityStatus.SOLD);
    PetDocument petDocumentBeethoven =
        createPetDocument("XYZ987", "Beethoven", PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE);

    petRepository.saveAll(Arrays.asList(petDocumentLassie, petDocumentAstro, petDocumentBeethoven));

    assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(3));

    UriComponentsBuilder uri =
        uriBuilder
            .getPetStoreBaseURI()
            .pathSegment("findByStatus")
            .queryParam("statuses", PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE.name());
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String expectedJsonBody =
        """
                                  {
                                    "pets": [
                                      {
                        "availabilityStatus": "Available For Purchase",
                                        "petId": "XYZ987",
                                        "vaccinationId": "AF54785412K",
                                        "name": "Beethoven",
                                        "petType": "Dog",
                                        "photoUrls": [
                                          "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                                        ]
                                      }
                                    ]
                                  }
                              """;

    String actualJsonBody = response.getBody();

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody));
  }

  @Test
  void should_UpdateExistingPetWithNewNameAndInformation_When_PatchEndpointIsCalled() {
    PetDocument petDocumentBeethoven =
        createPetDocument("XYZ987", "Beethoven", PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE);

    petRepository.save(petDocumentBeethoven);

    assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

    PetPatchRequest petPatch = new PetPatchRequest();
    petPatch.setId("XYZ987");
    petPatch.setName("Astro");
    List<@Valid PetInformationItem> additionalInformation =
        Collections.singletonList(testData.createPetInformationItem("Eye colour", "Green"));
    petPatch.setAdditionalInformation(additionalInformation);

    UriComponentsBuilder uri = uriBuilder.getPetStoreBaseURI();
    ResponseEntity<String> response = webClientExecutor.patch(uri, petPatch);

    String expectedJsonBody =
        """
                                    {
                        "availabilityStatus": "Available For Purchase",
                                      "petId": "XYZ987",
                                      "vaccinationId": "AF54785412K",
                                      "name": "Astro",
                                      "petType": "Dog",
                                      "photoUrls": [
                                        "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                                      ],
                                      "additionalInformation": [
                                        {
                                          "name": "Eye colour",
                                          "description": "Green"
                                        }
                                      ]
                                    }
                                """;

    String actualJsonBody = response.getBody();

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody));
  }

  @Test
  void should_SuccessfullyPurchaseAPet() {
    PetDocument savedPetDocument = petRepository.save(testData.createPetDocument());

    assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(savedPetDocument.getPetId());
    ResponseEntity<String> response = webClientExecutor.post(uri, testData.createCustomerRequest());

    String expectedJsonBody =
        """
                                    {
                        "availabilityStatus": "Pending Collection",
                                      "owner": {
                                        "username": "alex.stone",
                                        "firstName": "Alex",
                                        "lastName": "Stone",
                                        "email": "alex.stone@cgi.com",
                                        "address": {
                                          "street": "40 Princes Street",
                                          "city": "Edinburgh",
                                          "postCode": "EH2 2BY",
                                          "country": "United Kingdom"
                                        }
                                      },
                                      "petId": "KT1546",
                                      "vaccinationId": "AF54785412K",
                                      "name": "Fido",
                                      "petType": "Dog",
                                      "photoUrls": [
                                        "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                                      ]
                                    }
                                """;

    String actualJsonBody = response.getBody();
    String actualCustomerId = JsonPath.read(response.getBody(), "$.owner.customerId");

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        () -> assertThat(actualCustomerId, not(isEmptyOrNullString())));

    List<PetDocument> actualAllPetDocuments = petRepository.findAll();
    assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
    PetDocument allPetDocument = actualAllPetDocuments.getFirst();
    assertEquals("KT1546", allPetDocument.getPetId());
  }

  @Test
  void should_SuccessDeletePet() {
    PetDocument petDocument = testData.createPetDocument();
    String petId = petDocument.getPetId();
    petRepository.save(petDocument);

    assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(petId);
    ResponseEntity<String> response = webClientExecutor.delete(uri);

    String deletionMessage = JsonPath.read(response.getBody(), "$.message");

    assertAll(
        assertions.assertOkJsonResponse(response),
        () -> assertEquals("Successfully archived the Pet with Id: KT1546", deletionMessage));

    List<PetDocument> actualAllPetDocuments = petRepository.findAll();
    assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
    PetDocument actualPetDocument = actualAllPetDocuments.getFirst();
    assertEquals(PersistenceStatus.ARCHIVED.getValue(), actualPetDocument.getPersistenceStatus());
  }

  private PetDocument createPetDocument(
      String petId, String name, PetAvailabilityStatus PetAvailabilityStatus) {
    PetDocument petDocument = testData.createPetDocument(petId);

    petDocument.setPetId(petId);
    petDocument.setName(name);
    petDocument.setPetStatus(PetAvailabilityStatus.getValue());

    return petDocument;
  }
}
