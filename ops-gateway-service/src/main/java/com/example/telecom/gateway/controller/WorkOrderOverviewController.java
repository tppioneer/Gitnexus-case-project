package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.dto.WorkOrderSummary;
import com.example.telecom.gateway.service.DashboardAggregationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/workorders")
public class WorkOrderOverviewController {

    private final DashboardAggregationService dashboardAggregationService;

    public WorkOrderOverviewController(DashboardAggregationService dashboardAggregationService) {
        this.dashboardAggregationService = dashboardAggregationService;
    }

    @GetMapping("/summary")
    public ApiResponse<WorkOrderSummary> getWorkOrderSummary(
            @RequestParam(required = false, defaultValue = "all") String regionCode) {
        return ApiResponse.success(dashboardAggregationService.aggregateWorkOrders(regionCode));
    }
}
