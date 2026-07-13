package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.workorder.service.WorkOrderStatisticsService;
import com.example.telecom.workorder.service.WorkOrderTrendService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/workorders/statistics")
public class WorkOrderStatisticsController {

    private final WorkOrderStatisticsService workOrderStatisticsService;
    private final WorkOrderTrendService workOrderTrendService;

    public WorkOrderStatisticsController(WorkOrderStatisticsService workOrderStatisticsService,
                                          WorkOrderTrendService workOrderTrendService) {
        this.workOrderStatisticsService = workOrderStatisticsService;
        this.workOrderTrendService = workOrderTrendService;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview() {
        return ApiResponse.success(workOrderStatisticsService.getOverview());
    }

    @GetMapping("/by-status")
    public ApiResponse<Map<String, Long>> getByStatus() {
        return ApiResponse.success(workOrderStatisticsService.getStatusDistribution());
    }

    @GetMapping("/by-priority")
    public ApiResponse<Map<String, Long>> getByPriority() {
        Map<String, Long> result = new HashMap<>();
        workOrderStatisticsService.getPriorityDistribution()
                .forEach((priority, count) -> result.put(priority.name(), count));
        return ApiResponse.success(result);
    }

    @GetMapping("/by-category")
    public ApiResponse<Map<String, Long>> getByCategory() {
        return ApiResponse.success(workOrderStatisticsService.getCategoryDistribution());
    }

    @GetMapping("/trend")
    public ApiResponse<Map<String, Object>> getTrend() {
        Map<String, Object> trend = new HashMap<>();
        trend.put("dailyCreationRate", workOrderTrendService.getDailyCreationRate());
        trend.put("slaComplianceRate", workOrderTrendService.getSlaComplianceRate());
        trend.put("averageResolutionTimeMinutes", workOrderStatisticsService.averageResolutionTimeMinutes());
        return ApiResponse.success(trend);
    }

    @GetMapping("/operator/{operatorId}")
    public ApiResponse<Map<String, Object>> getOperatorStats(@PathVariable String operatorId) {
        return ApiResponse.success(workOrderStatisticsService.getOperatorStats(operatorId));
    }
}
