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
import com.cgi.example.petstore.utils.TestData;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Tag("integration")
public class CompleteFlowBlackBoxIntegrationTest extends BaseIntegrationTest {

  private static final List<String> ALL_PET_STATUSES =
      Arrays.stream(PetAvailabilityStatus.values()).map(Enum::name).collect(Collectors.toList());

  private final TestData testData = new TestData();

  @Test
  void should_Successfully_Add_Modify_Find_Update_PurchaseAPet() {
    verifyNotPetsOfAnyStatusesAreAlreadyPresent();

    String newPetId = addANewPet();

    retrieveNewlyAddedPetById(newPetId);

    retrieveNewlyAddedPetByStatus(newPetId);

    updatePetDetails(newPetId);

    String generatedCustomerId = purchaseThePet(newPetId);

    verifyThePetHasBeenPurchased(generatedCustomerId, newPetId);
  }

  private void verifyThePetHasBeenPurchased(String customerId, String petId) {
    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(petId);
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String expectedJsonBody =
        """
                              {
                                "vaccinations": [
                                  {
                                    "name": "Parainfluenza",
                                    "dateOfAdminister": "2017-07-21"
                                  },
                                  {
                                    "name": "Bordetella bronchiseptica",
                                    "dateOfAdminister": "2017-09-05"
                                  },
                                  {
                                    "name": "Canine Adenovirus",
                                    "dateOfAdminister": "2016-01-25"
                                  }
                                ],
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
    String actualCustomerId = JsonPath.read(response.getBody(), "$.owner.customerId");

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        () -> assertEquals(petId, extractPetId(actualJsonBody)),
        () -> assertEquals(customerId, actualCustomerId));
  }

  private String purchaseThePet(String petId) {
    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(petId);
    ResponseEntity<String> response = webClientExecutor.post(uri, testData.createCustomerRequest());

    String expectedJsonBody =
        """
                              {
                                "vaccinations": [
                                  {
                                    "name": "Parainfluenza",
                                    "dateOfAdminister": "2017-07-21"
                                  },
                                  {
                                    "name": "Bordetella bronchiseptica",
                                    "dateOfAdminister": "2017-09-05"
                                  },
                                  {
                                    "name": "Canine Adenovirus",
                                    "dateOfAdminister": "2016-01-25"
                                  }
                                ],
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
    String actualCustomerId = JsonPath.read(response.getBody(), "$.owner.customerId");

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        () -> assertEquals(petId, extractPetId(actualJsonBody)),
        () -> assertThat(actualCustomerId, not(isEmptyOrNullString())));

    return actualCustomerId;
  }

  private void updatePetDetails(String petId) {
    PetPatchRequest petPatch = new PetPatchRequest();
    petPatch.setId(petId);
    petPatch.setName("Astro");
    List<@Valid PetInformationItem> additionalInformation =
        Collections.singletonList(testData.createPetInformationItem("Eye colour", "Green"));
    petPatch.setAdditionalInformation(additionalInformation);

    UriComponentsBuilder uri = uriBuilder.getPetStoreBaseURI();
    ResponseEntity<String> response = webClientExecutor.patch(uri, petPatch);

    String expectedJsonBody =
        """
                         {
                           "vaccinations": [
                             {
                               "name": "Parainfluenza",
                               "dateOfAdminister": "2017-07-21"
                             },
                             {
                               "name": "Bordetella bronchiseptica",
                               "dateOfAdminister": "2017-09-05"
                             },
                             {
                               "name": "Canine Adenovirus",
                               "dateOfAdminister": "2016-01-25"
                             }
                           ],
                           "availabilityStatus": "Available For Purchase",
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
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        () -> assertEquals(petId, extractPetId(actualJsonBody)));
  }

  private void retrieveNewlyAddedPetByStatus(String petId) {
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
                                 "vaccinations": [
                                   {
                                     "name": "Parainfluenza",
                                     "dateOfAdminister": "2017-07-21"
                                   },
                                   {
                                     "name": "Bordetella bronchiseptica",
                                     "dateOfAdminister": "2017-09-05"
                                   },
                                   {
                                     "name": "Canine Adenovirus",
                                     "dateOfAdminister": "2016-01-25"
                                   }
                                 ],
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
                             ]
                           }
                        """;

    String actualJsonBody = response.getBody();

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        assertions.assertJsonPathEquals(petId, "$.pets[0].petId", actualJsonBody));
  }

  private void retrieveNewlyAddedPetById(String petId) {
    UriComponentsBuilder uri = uriBuilder.getPetStoreURIFor(petId);
    ResponseEntity<String> response = webClientExecutor.get(uri);

    String expectedJsonBody =
        """
                              {
                                "vaccinations": [
                                  {
                                    "name": "Parainfluenza",
                                    "dateOfAdminister": "2017-07-21"
                                  },
                                  {
                                    "name": "Bordetella bronchiseptica",
                                    "dateOfAdminister": "2017-09-05"
                                  },
                                  {
                                    "name": "Canine Adenovirus",
                                    "dateOfAdminister": "2016-01-25"
                                  }
                                ],
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

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        () -> assertEquals(petId, extractPetId(actualJsonBody)));
  }

  private String addANewPet() {
    NewPetRequest petToAdd = testData.createNewPetRequest();
    UriComponentsBuilder uri = uriBuilder.getPetStoreBaseURI();

    ResponseEntity<String> response = webClientExecutor.post(uri, petToAdd);

    String expectedJsonBody =
        """
                             {
                               "vaccinations": [
                                 {
                                   "name": "Parainfluenza",
                                   "dateOfAdminister": "2017-07-21"
                                 },
                                 {
                                   "name": "Bordetella bronchiseptica",
                                   "dateOfAdminister": "2017-09-05"
                                 },
                                 {
                                   "name": "Canine Adenovirus",
                                   "dateOfAdminister": "2016-01-25"
                                 }
                               ],
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
    String actualPetId = extractPetId(actualJsonBody);

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
        () -> assertThat(actualPetId, not(isEmptyOrNullString())));

    return actualPetId;
  }

  private void verifyNotPetsOfAnyStatusesAreAlreadyPresent() {
    UriComponentsBuilder uri =
        uriBuilder
            .getPetStoreBaseURI()
            .pathSegment("findByStatus")
            .queryParam("statuses", ALL_PET_STATUSES);

    ResponseEntity<String> response = webClientExecutor.get(uri);

    String expectedJsonBody = "{ }";
    String actualJsonBody = response.getBody();

    assertAll(
        assertions.assertOkJsonResponse(response),
        assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody));
  }

  private String extractPetId(String actualJsonBody) {
    return JsonPath.read(actualJsonBody, "$.petId");
  }
}
