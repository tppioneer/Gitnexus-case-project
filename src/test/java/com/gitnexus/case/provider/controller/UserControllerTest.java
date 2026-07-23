package com.gitnexus.case.provider.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUser_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/rest/v2/users/789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("789"))
                .andExpect(jsonPath("$.name").value("User-789"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        String requestBody = "{\"name\": \"Test User\"}";
        mockMvc.perform(post("/rest/v2/users")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
