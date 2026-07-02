package com.example.telecom.gateway;

import com.example.telecom.gateway.dto.RegionFilterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionFilterRequestTest {

    @Test
    void shouldSetAndGetRegionCode() {
        RegionFilterRequest request = new RegionFilterRequest();
        request.setRegionCode("EAST");
        assertEquals("EAST", request.getRegionCode());
    }

    @Test
    void shouldDefaultToNull() {
        RegionFilterRequest request = new RegionFilterRequest();
        assertNull(request.getRegionCode());
    }
}
