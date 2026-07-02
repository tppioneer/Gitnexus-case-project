package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.dto.DeviceHealthSummary;
import com.example.telecom.gateway.service.DashboardAggregationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/devices")
public class DeviceOverviewController {

    private final DashboardAggregationService dashboardAggregationService;

    public DeviceOverviewController(DashboardAggregationService dashboardAggregationService) {
        this.dashboardAggregationService = dashboardAggregationService;
    }

    @GetMapping("/summary")
    public ApiResponse<DeviceHealthSummary> getDeviceSummary(
            @RequestParam(required = false, defaultValue = "all") String regionCode) {
        return ApiResponse.success(dashboardAggregationService.aggregateDeviceHealth(regionCode));
    }
}
