package mygov.task02;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
class CustomerApiTest {

    private final MockMvc mockMvc;

    CustomerApiTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void shouldCreateAndRetrieveCustomer() throws Exception {
        String email = "api-" + UUID.randomUUID() + "@example.com";

        String requestBody = """
                {
                  "name": "Rauf Asadli",
                  "email": "%s",
                  "phone": "+994501234567"
                }
                """.formatted(email);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Rauf Asadli"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").value("+994501234567"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        String resourcePath = URI.create(location).getPath();

        try {
            long id = Long.parseLong(
                    resourcePath.substring(resourcePath.lastIndexOf('/') + 1));

            mockMvc.perform(get(resourcePath))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("Rauf Asadli"))
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.phone").value("+994501234567"));
        } finally {
            mockMvc.perform(delete(resourcePath))
                    .andExpect(status().isNoContent());
        }

    }

    @Test
    void shouldGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNumber());


    }

    @Test
    void shouldUpdateExistingCustomer() throws Exception {
        String email = "update-" + UUID.randomUUID() + "@example.com";

        String createBody = """
                {
                "name" : "Original name",
                "email" : "%s",
                "phone" : "+994501234567"
                }
                """.formatted(email);

        MvcResult result = mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        String resourcePath = URI.create(location).getPath();

        try {
            String updateEmail = "updated-" + UUID.randomUUID() + "@example.com";

            String updateBody = """
                    {
                    "name" : "Updated Name",
                    "email" : "%s",
                    "phone" : "+994509999999"
                    }
                    """.formatted(updateEmail);

            mockMvc.perform(put(resourcePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateBody))
                    .andExpect(status().isOk());

            mockMvc.perform(get(resourcePath))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.email").value(updateEmail))
                    .andExpect(jsonPath("$.phone").value("+994509999999"));


        } finally {
            mockMvc.perform(delete(resourcePath))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    void shouldRejectBlankName() throws Exception {
        String requestBody = """
                {
                "name" : "",
                "email" : "valid@example.com",
                "phone" : "+994501234567"
                }
                """;

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/customers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").exists());
    }

    @Test
    void shouldReturn409WhenDuplicateEmailFound() throws Exception {

        String requestBody = """
                {
                "name" : "Another Customer",
                "email" : "faiq@idda.az",
                "phone" : "+994509999999"
                }
                """;

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());


    }
}