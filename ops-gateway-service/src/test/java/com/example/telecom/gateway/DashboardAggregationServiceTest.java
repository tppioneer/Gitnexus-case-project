package com.example.telecom.gateway;

import com.example.telecom.gateway.client.*;
import com.example.telecom.gateway.dto.*;
import com.example.telecom.gateway.service.DashboardAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DashboardAggregationServiceTest {

    private DashboardAggregationService service;

    @BeforeEach
    void setUp() {
        service = new DashboardAggregationService(
                new DeviceClient(), new AlarmClient(), new WorkOrderClient());
    }

    @Test
    void shouldAggregateDeviceHealth() {
        DeviceHealthSummary summary = service.aggregateDeviceHealth("EAST");
        assertNotNull(summary);
        assertEquals("EAST", summary.getRegionCode());
    }

    @Test
    void shouldAggregateAlarms() {
        AlarmSummary summary = service.aggregateAlarms("WEST");
        assertNotNull(summary);
        assertEquals("WEST", summary.getRegionCode());
    }

    @Test
    void shouldAggregateWorkOrders() {
        WorkOrderSummary summary = service.aggregateWorkOrders("SOUTH");
        assertNotNull(summary);
        assertEquals("SOUTH", summary.getRegionCode());
    }
}
