package com.example.telecom.change.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the supplementary MVC mapping conditions (R13+).
 * Validates mapping conditions beyond HTTP method + path.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MvcConditionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void r13_getSummary_producesJson() throws Exception {
        mockMvc.perform(get("/api/network-changes/CHG-001/summary")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void r14_importXml_consumesXml() throws Exception {
        mockMvc.perform(post("/api/network-changes/import")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<import/>"))
                .andExpect(status().isOk());
    }

    @Test
    void r14_importXml_rejectsJson() throws Exception {
        mockMvc.perform(post("/api/network-changes/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void r15_filter_requiresRegionParam() throws Exception {
        mockMvc.perform(get("/api/network-changes/filter").param("region", "east"))
                .andExpect(status().isOk());
    }

    @Test
    void r15_filter_missingParam_returns404() throws Exception {
        // Without the required "region" param, the mapping condition fails
        // and Spring reports no matching handler → 404.
        mockMvc.perform(get("/api/network-changes/filter"))
                .andExpect(status().isNotFound());
    }

    @Test
    void r16_admin_requiresHeader() throws Exception {
        mockMvc.perform(get("/api/network-changes/admin").header("X-Admin", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void r16_admin_wrongHeader_returns404() throws Exception {
        mockMvc.perform(get("/api/network-changes/admin").header("X-Admin", "false"))
                .andExpect(status().isNotFound());
    }

    @Test
    void r17_toggle_acceptsGet() throws Exception {
        mockMvc.perform(get("/api/network-changes/CHG-001/toggle"))
                .andExpect(status().isOk());
    }

    @Test
    void r17_toggle_acceptsPost() throws Exception {
        mockMvc.perform(post("/api/network-changes/CHG-001/toggle"))
                .andExpect(status().isOk());
    }
}
