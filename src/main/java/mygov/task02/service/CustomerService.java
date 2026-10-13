package mygov.task02.service;


import mygov.task02.dto.CreateCustomerRequest;
import mygov.task02.dto.UpdateCustomerRequest;
import mygov.task02.exception.DuplicateResourceException;
import mygov.task02.exception.ResourceNotFoundException;
import mygov.task02.model.Customer;
import mygov.task02.repository.CustomerRepository;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer id with 99 not found"));
    }

    @Transactional
    public Customer update(Long id, UpdateCustomerRequest customerRequest) {
        boolean updated = customerRepository.update(id, customerRequest);

        if(!updated) {
            throw new ResourceNotFoundException("Customer with id " + id + " not found");
        }

        return findById(id);
    }

    @Transactional
    public void delete(Long id) {
        boolean deleted = customerRepository.delete(id);

        if(!deleted) {
            throw new ResourceNotFoundException("No customer with id " + id + " to delete");
        }
    }

    @Transactional
    public Customer create(CreateCustomerRequest customerRequest) {
        boolean created = customerRepository.emailExists(customerRequest.email());
        if(created) {
            throw new DuplicateResourceException("Customer with email " + customerRequest.email() + " already exists");
        }
        return  customerRepository.save(customerRequest);
    }



}
