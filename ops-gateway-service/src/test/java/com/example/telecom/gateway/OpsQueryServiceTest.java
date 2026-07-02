package com.example.telecom.gateway;

import com.example.telecom.gateway.client.*;
import com.example.telecom.gateway.dto.*;
import com.example.telecom.gateway.mapper.DashboardResponseMapper;
import com.example.telecom.gateway.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpsQueryServiceTest {

    private OpsQueryService opsQueryService;

    @BeforeEach
    void setUp() {
        DashboardAggregationService aggregationService = new DashboardAggregationService(
                new DeviceClient(), new AlarmClient(), new WorkOrderClient());
        DashboardResponseMapper mapper = new DashboardResponseMapper();
        opsQueryService = new OpsQueryService(aggregationService, mapper);
    }

    @Test
    void shouldBuildDashboardForRegion() {
        DashboardResponse response = opsQueryService.buildDashboard("EAST");
        assertNotNull(response);
        assertNotNull(response.getDeviceHealth());
        assertNotNull(response.getAlarms());
        assertNotNull(response.getWorkOrders());
        assertTrue(response.getTimestamp() > 0);
    }

    @Test
    void shouldBuildDashboardForDifferentRegions() {
        DashboardResponse east = opsQueryService.buildDashboard("EAST");
        DashboardResponse west = opsQueryService.buildDashboard("WEST");

        assertNotNull(east);
        assertNotNull(west);
        assertEquals("EAST", east.getDeviceHealth().getRegionCode());
        assertEquals("WEST", west.getDeviceHealth().getRegionCode());
    }

    @Test
    void shouldBuildDashboardForAllRegion() {
        DashboardResponse all = opsQueryService.buildDashboard("all");
        assertNotNull(all);
        assertEquals("all", all.getDeviceHealth().getRegionCode());
    }
}
