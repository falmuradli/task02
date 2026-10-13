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
                  "name": "Nazli Nasirli",
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
                .andExpect(jsonPath("$.name").value("Nazli Nasirli"))
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
                    .andExpect(jsonPath("$.name").value("Nazli Nasirli"))
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.phone").value("+994501234567"));
        } finally {
            mockMvc.perform(delete(resourcePath))
                    .andExpect(status().isNoContent());
        }
    }
}