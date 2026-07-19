package com.example.telecom.change.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Case H: Spring MVC Route Matrix Test.
 * Verifies all R01-R12 routes are correctly mapped with proper
 * HTTP methods, paths, class prefixes, and composed annotations.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CaseHRouteMatrixTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void r01_getById_classPrefixWithPathVariable() throws Exception {
        // R01: GET /api/network-changes/{id}
        mockMvc.perform(get("/api/network-changes/CHG-001"))
                .andExpect(status().isNotFound()); // no data but route exists
    }

    @Test
    void r02_createPost_emptyPathWithConsumes() throws Exception {
        // R02: POST /api/network-changes (empty path + consumes JSON)
        mockMvc.perform(post("/api/network-changes")
                        .contentType("application/json")
                        .content("{\"title\":\"test\",\"regionCode\":\"east\",\"deviceFamily\":\"ROUTER\",\"risk\":\"LOW\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void r03_updatePut_pathVariable() throws Exception {
        // R03: PUT /api/network-changes/{id}
        mockMvc.perform(put("/api/network-changes/CHG-001")
                        .contentType("application/json")
                        .content("{\"title\":\"updated\",\"regionCode\":\"east\",\"deviceFamily\":\"ROUTER\",\"risk\":\"LOW\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void r04_patchRisk_valueMapping() throws Exception {
        // R04: PATCH /api/network-changes/{id}/risk
        mockMvc.perform(patch("/api/network-changes/CHG-001/risk")
                        .contentType("application/json")
                        .content("{\"planId\":\"CHG-001\",\"newRisk\":\"HIGH\",\"reason\":\"test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void r05_delete_pathVariable() throws Exception {
        // R05: DELETE /api/network-changes/{id}
        mockMvc.perform(delete("/api/network-changes/CHG-001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void r06_legacyPreview_anyMethod() throws Exception {
        // R06: ANY /legacy/preview — test with GET
        mockMvc.perform(get("/legacy/preview"))
                .andExpect(status().isOk());
        // Also with POST — should still work (ANY method)
        mockMvc.perform(post("/legacy/preview"))
                .andExpect(status().isOk());
    }

    @Test
    void r07_legacySearch_explicitGetMethod() throws Exception {
        // R07: GET /legacy/search (explicit method=GET)
        mockMvc.perform(get("/legacy/search").param("q", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void r08_arrayPath_active() throws Exception {
        // R08: GET /api/network-changes/active (array path, first entry)
        mockMvc.perform(get("/api/network-changes/active"))
                .andExpect(status().isOk());
    }

    @Test
    void r08_arrayPath_open() throws Exception {
        // R08: GET /api/network-changes/open (array path, second entry)
        mockMvc.perform(get("/api/network-changes/open"))
                .andExpect(status().isOk());
    }

    @Test
    void r09_composedAnnotation_audit() throws Exception {
        // R09: GET /api/network-changes/{id}/audit via @OpsReadEndpoint
        mockMvc.perform(get("/api/network-changes/CHG-001/audit"))
                .andExpect(status().isOk());
    }

    @Test
    void r09_post_rejected_becauseOpsReadEndpointIsGetOnly() throws Exception {
        // @OpsReadEndpoint carries @RequestMapping(method=GET); POST must not match
        mockMvc.perform(post("/api/network-changes/CHG-001/audit"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void r09_put_rejected_becauseOpsReadEndpointIsGetOnly() throws Exception {
        mockMvc.perform(put("/api/network-changes/CHG-001/audit"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void r10_fullyQualifiedAnnotation() throws Exception {
        // R10: GET /qualified — fully-qualified @GetMapping
        mockMvc.perform(get("/qualified"))
                .andExpect(status().isOk());
    }

    @Test
    void r11_constantPath() throws Exception {
        // R11: GET /api/network-changes/pending — from ChangeRoutes.PENDING constant
        mockMvc.perform(get("/api/network-changes/pending"))
                .andExpect(status().isOk());
    }

    @Test
    void r12_classPrefixOnly() throws Exception {
        // R12: GET /api/network-changes — class prefix with no method path
        mockMvc.perform(get("/api/network-changes"))
                .andExpect(status().isOk());
    }

    @Test
    void fakeRoute_isNotMapped() throws Exception {
        // Negative: "/api/network-changes/fake" from log message is NOT a route
        mockMvc.perform(get("/api/network-changes/fake"))
                .andExpect(status().isNotFound());
    }
}
