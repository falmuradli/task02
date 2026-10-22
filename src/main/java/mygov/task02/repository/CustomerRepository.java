package mygov.task02.repository;


import mygov.task02.dto.CreateCustomerRequest;
import mygov.task02.dto.UpdateCustomerRequest;
import mygov.task02.model.Customer;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcClient jdbcClient;

    public CustomerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Customer> findAll() {
        return jdbcClient
                .sql("""
                        SELECT id, name, email, phone FROM customers ORDER BY id""")

                .query(Customer.class)
                .list();
    }

    public Optional<Customer> findById(Long id) {
        return jdbcClient
                .sql("""
                        SELECT id, name, email, phone FROM customers WHERE id = :id""")
                .param("id", id)
                .query(Customer.class)
                .optional();
    }

    public boolean emailExists(String email) {
        return jdbcClient
                .sql("""
                        SELECT id, name, email, phone FROM customers WHERE email = :email""")
                .param("email", email)
                .query(Customer.class)
                .optional()
                .isPresent();

    }

    public boolean update(Long id, UpdateCustomerRequest updateCustomerRequest) {
        int rows =  jdbcClient
                .sql("""
                        UPDATE customers SET name = :name, email = :email, phone = :phone WHERE id = :id""")
                .param("name", updateCustomerRequest.name())
                .param("email", updateCustomerRequest.email())
                .param("phone", updateCustomerRequest.phone())
                .param("id", id)
                .update();

        return rows > 0;
    }

    public Customer save(CreateCustomerRequest createCustomerRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient
                .sql("""
                        INSERT INTO customers (name, email, phone) VALUES (:name, :email, :phone)""")
                .param("name", createCustomerRequest.name())
                .param("email", createCustomerRequest.email())
                .param("phone", createCustomerRequest.phone())
                .update(keyHolder, "id");

        Long id = keyHolder.getKey().longValue();

        return new Customer(
                id,
                createCustomerRequest.name(),
                createCustomerRequest.email(),
                createCustomerRequest.phone());
    }

    public boolean delete(Long id) {
        int rows = jdbcClient
                .sql("""
                        DELETE FROM customers WHERE id = :id""")
                .param("id", id)
                .update();

        return rows > 0;
    }

}
