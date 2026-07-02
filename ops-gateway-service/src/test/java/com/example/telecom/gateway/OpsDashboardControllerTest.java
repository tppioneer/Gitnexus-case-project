package com.example.telecom.gateway;

import com.example.telecom.gateway.client.*;
import com.example.telecom.gateway.controller.OpsDashboardController;
import com.example.telecom.gateway.mapper.DashboardResponseMapper;
import com.example.telecom.gateway.service.*;
import com.example.telecom.common.api.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpsDashboardControllerTest {

    private OpsDashboardController controller;

    @BeforeEach
    void setUp() {
        DashboardAggregationService aggregationService = new DashboardAggregationService(
                new DeviceClient(), new AlarmClient(), new WorkOrderClient());
        DashboardResponseMapper mapper = new DashboardResponseMapper();
        OpsQueryService queryService = new OpsQueryService(aggregationService, mapper);
        GatewayAuditService auditService = new GatewayAuditService();
        controller = new OpsDashboardController(queryService, auditService);
    }

    @Test
    void shouldReturnDashboard() {
        ApiResponse<?> response = controller.getDashboard("EAST");
        assertEquals(200, response.getCode());
    }
}
