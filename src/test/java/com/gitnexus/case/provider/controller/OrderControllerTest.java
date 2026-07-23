package com.gitnexus.case.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrder_shouldReturnOrder() throws Exception {
        mockMvc.perform(get("/rest/v1/orders/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Order-123"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void batchOrders_shouldProcessBatch() throws Exception {
        BatchOrderRequest request = new BatchOrderRequest(List.of("1", "2", "3"));
        mockMvc.perform(post("/rest/v1/orders/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.message").value("Batch processed"));
    }

    @Test
    void deleteOrder_shouldReturnDeleteResponse() throws Exception {
        mockMvc.perform(delete("/rest/v1/orders/456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("456"))
                .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("New Order", 50.0);
        mockMvc.perform(post("/rest/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Order"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void searchOrders_shouldReturnSearchResults() throws Exception {
        mockMvc.perform(get("/rest/v1/orders/search")
                        .param("status", "COMPLETED")
                        .param("region", "NORTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.region").value("NORTH"))
                .andExpect(jsonPath("$.total").value(5));
    }

    @Test
    void getAmbiguous_shouldReturnFromProvider() throws Exception {
        mockMvc.perform(get("/rest/v1/ambiguous/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.source").value("provider-ambiguous-test-id"));
    }
}
