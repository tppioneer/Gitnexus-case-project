package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.dto.AlarmSummary;
import com.example.telecom.gateway.service.DashboardAggregationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/alarms")
public class AlarmOverviewController {

    private final DashboardAggregationService dashboardAggregationService;

    public AlarmOverviewController(DashboardAggregationService dashboardAggregationService) {
        this.dashboardAggregationService = dashboardAggregationService;
    }

    @GetMapping("/summary")
    public ApiResponse<AlarmSummary> getAlarmSummary(
            @RequestParam(required = false, defaultValue = "all") String regionCode) {
        return ApiResponse.success(dashboardAggregationService.aggregateAlarms(regionCode));
    }
}
