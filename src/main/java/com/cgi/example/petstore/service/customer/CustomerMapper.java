package com.cgi.example.petstore.service.customer;

import com.cgi.example.petstore.model.Address;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.CustomerResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

  public CustomerDocument mapToCustomerDocument(CustomerRequest customerToMap) {
    return CustomerDocument.builder()
        .customerId(customerToMap.getCustomerId())
        .username(customerToMap.getUsername())
        .firstName(customerToMap.getFirstName())
        .lastName(customerToMap.getLastName())
        .email(customerToMap.getEmail())
        .address(mapToAddress(customerToMap.getAddress()))
        .build();
  }

  private CustomerAddressPersistenceType mapToAddress(@NotNull @Valid Address address) {
    return CustomerAddressPersistenceType.builder()
        .street(address.getStreet())
        .city(address.getCity())
        .postCode(address.getPostCode())
        .country(address.getCountry())
        .build();
  }

  public CustomerResponse mapToCustomerResponse(CustomerDocument customerDocumentToMap) {
    CustomerResponse customerResponse = new CustomerResponse();
    customerResponse.setCustomerId(customerDocumentToMap.getCustomerId());
    customerResponse.setUsername(customerDocumentToMap.getUsername());
    customerResponse.setFirstName(customerDocumentToMap.getFirstName());
    customerResponse.setLastName(customerDocumentToMap.getLastName());
    customerResponse.setEmail(customerDocumentToMap.getEmail());
    customerResponse.setAddress(mapToAddress(customerDocumentToMap.getAddress()));
    return customerResponse;
  }

  private Address mapToAddress(CustomerAddressPersistenceType addressToMap) {
    Address address = new Address();
    address.setStreet(addressToMap.getStreet());
    address.setCity(addressToMap.getCity());
    address.setPostCode(addressToMap.getPostCode());
    address.setCountry(addressToMap.getCountry());
    return address;
  }
}
