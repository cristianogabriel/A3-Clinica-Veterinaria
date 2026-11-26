package com.clinica.ibmr.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void privateEndpointsRejectAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/tutores")).andExpect(status().isUnauthorized());
    }

    @Test
    void openApiDocumentationIsPubliclyAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API privada da Clínica Veterinária"));
    }
}
