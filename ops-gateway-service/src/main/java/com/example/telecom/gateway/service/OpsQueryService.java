package com.example.telecom.gateway.service;

import com.example.telecom.gateway.dto.DashboardResponse;
import com.example.telecom.gateway.mapper.DashboardResponseMapper;

/**
 * Orchestrates dashboard data aggregation and mapping.
 */
public class OpsQueryService {

    private final DashboardAggregationService dashboardAggregationService;
    private final DashboardResponseMapper dashboardResponseMapper;

    public OpsQueryService(DashboardAggregationService dashboardAggregationService,
                            DashboardResponseMapper dashboardResponseMapper) {
        this.dashboardAggregationService = dashboardAggregationService;
        this.dashboardResponseMapper = dashboardResponseMapper;
    }

    public DashboardResponse buildDashboard(String regionCode) {
        return dashboardResponseMapper.toResponse(
                dashboardAggregationService.aggregateDeviceHealth(regionCode),
                dashboardAggregationService.aggregateAlarms(regionCode),
                dashboardAggregationService.aggregateWorkOrders(regionCode)
        );
    }
}
