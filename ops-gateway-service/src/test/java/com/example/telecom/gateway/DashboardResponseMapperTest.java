package com.example.telecom.gateway;

import com.example.telecom.gateway.dto.*;
import com.example.telecom.gateway.mapper.DashboardResponseMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DashboardResponseMapperTest {

    @Test
    void shouldMapToDashboardResponse() {
        DashboardResponseMapper mapper = new DashboardResponseMapper();
        DeviceHealthSummary health = new DeviceHealthSummary();
        health.setRegionCode("EAST");
        AlarmSummary alarms = new AlarmSummary();
        alarms.setRegionCode("EAST");
        WorkOrderSummary orders = new WorkOrderSummary();
        orders.setRegionCode("EAST");

        DashboardResponse response = mapper.toResponse(health, alarms, orders);
        assertNotNull(response);
        assertEquals("EAST", response.getDeviceHealth().getRegionCode());
        assertTrue(response.getTimestamp() > 0);
    }
}
