package mygov.task02.controller;

import jakarta.validation.Valid;
import mygov.task02.dto.CreateCustomerRequest;
import mygov.task02.dto.UpdateCustomerRequest;
import mygov.task02.model.Customer;
import mygov.task02.repository.CustomerRepository;
import mygov.task02.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.Collection;


@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public Collection<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest) {
        Customer created = customerService.create(customerRequest);

        URI location = URI.create("/api/v1/customers/" + created.id());

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest customerRequest) {
        return customerService.update(id, customerRequest);
    }


}
