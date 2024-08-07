package com.cgi.example.petstore.service.customer;

import com.cgi.example.petstore.exception.NotFoundException;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.CustomerResponse;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDataStoreService {

  private final CustomerMapper customerMapper;
  private final CustomerRepository customerRepository;

  public CustomerResponse insertIfAbsent(CustomerRequest customerRequest) {
    String customerId = customerRequest.getCustomerId();
    if (Objects.nonNull(customerId)) {
      Optional<CustomerDocument> optionalCustomerDocument = customerRepository.findById(customerId);
      if (optionalCustomerDocument.isPresent()) {
        return customerMapper.mapToCustomerResponse(optionalCustomerDocument.get());
      }
    }

    CustomerDocument customerDocument = customerMapper.mapToCustomerDocument(customerRequest);
    CustomerDocument savedCustomerDocument = customerRepository.insert(customerDocument);
    return customerMapper.mapToCustomerResponse(savedCustomerDocument);
  }

  public CustomerResponse retrieveCustomer(String customerId) {
    Optional<CustomerDocument> optionalCustomerDocument = customerRepository.findById(customerId);
    if (optionalCustomerDocument.isEmpty()) {
      String message = "Unable to find the Customer with customerId: [%s]".formatted(customerId);
      throw new NotFoundException(message);
    }

    CustomerDocument customerDocument = optionalCustomerDocument.get();
    return customerMapper.mapToCustomerResponse(customerDocument);
  }
}
