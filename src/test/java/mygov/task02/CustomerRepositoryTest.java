package mygov.task02;

import mygov.task02.dto.CreateCustomerRequest;
import mygov.task02.dto.UpdateCustomerRequest;
import mygov.task02.model.Customer;
import mygov.task02.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;



import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import(CustomerRepository.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CustomerRepositoryTest {

    private final CustomerRepository customerRepository;

    CustomerRepositoryTest(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Test
    void findAll_returnSeededCustomers() {
        List<Customer> customerList = customerRepository.findAll();

        assertEquals(3, customerList.size());
        customerList.stream()
                .map(Customer::email)
                .toList();
    }

    @Test
    void findById_existingId_returnsCustomer(){
        Optional<Customer> result = customerRepository.findById(1L);
        assertThat(result).isPresent();
        Customer customer = result.get();
        assertEquals("faiq@idda.az", customer.email());
    }

    @Test
    void findById_missingId_returnsEmpty() {
        Optional<Customer> result = customerRepository.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void emailExists_returnsEmail() {
        boolean result = customerRepository.emailExists("faiq@idda.az");
        assertThat(result).isTrue();
    }

    @Test
    void emailExists_returnsEmpty() {
        boolean result = customerRepository.emailExists("resad@idda.az");
        assertThat(result).isFalse();
    }

    @Test
    void save_Customer_persistsAndReturnsCustomer() {
        CreateCustomerRequest customerRequest =
                new CreateCustomerRequest("Test User", "test.user.@example.com", "+994501234567");

        Customer saved = customerRepository.save(customerRequest);

        assertThat(saved.id()).isNotNull();
        Optional<Customer> result = customerRepository.findById(saved.id());
        assertThat(result.isPresent());
        Customer customer = result.get();
        assertEquals(customerRequest.email(), customer.email());
    }

    @Test
    void update_existingId_changesAllFieldsAndReturnsTrue() {
        UpdateCustomerRequest request =
                new UpdateCustomerRequest("New Name", "new.email@example.test", "+994509999999");

        boolean updated = customerRepository.update(1L, request);

        assertThat(updated).isTrue();

        Optional<Customer> result = customerRepository.findById(1L);
        assertThat(result).isPresent();
        Customer customer = result.get();
        assertEquals(request.name(), customer.name());
        assertEquals(request.email(), customer.email());
        assertEquals(request.phone(), customer.phone());
    }

    @Test
    void update_missingId_returnsFalse() {
        UpdateCustomerRequest request =
                new UpdateCustomerRequest("Nobody", "nobody@example.test", "+994500000000");

        boolean updated = customerRepository.update(999L, request);

        assertThat(updated).isFalse();
    }


    @Test
    void delete_existingId_removesRowAndReturnsTrue() {
        boolean deleted = customerRepository.delete(1L);

        assertThat(deleted).isTrue();
        assertThat(customerRepository.findById(1L)).isEmpty();
    }

    @Test
    void delete_missingId_returnsFalse() {
        boolean deleted = customerRepository.delete(999L);

        assertThat(deleted).isFalse();
    }
}
