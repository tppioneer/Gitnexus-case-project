package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.service.DashboardDrilldownService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardDrilldownController {

    private final DashboardDrilldownService drilldownService;

    public DashboardDrilldownController(DashboardDrilldownService drilldownService) {
        this.drilldownService = drilldownService;
    }

    @GetMapping("/regions/{code}/drilldown")
    public ApiResponse<Map<String, Object>> getRegionDrilldown(@PathVariable String code) {
        Map<String, Object> drilldown = drilldownService.getRegionDrilldown(code);
        return ApiResponse.success(drilldown);
    }

    @GetMapping("/devices/{deviceId}/drilldown")
    public ApiResponse<Map<String, Object>> getDeviceDrilldown(@PathVariable String deviceId) {
        Map<String, Object> drilldown = drilldownService.getDeviceDrilldown(deviceId);
        return ApiResponse.success(drilldown);
    }

    @GetMapping("/alarms/{alarmId}/drilldown")
    public ApiResponse<Map<String, Object>> getAlarmDrilldown(@PathVariable String alarmId) {
        Map<String, Object> drilldown = drilldownService.getAlarmDrilldown(alarmId);
        return ApiResponse.success(drilldown);
    }

    @GetMapping("/topology/{nodeId}/drilldown")
    public ApiResponse<Map<String, Object>> getTopologyDrilldown(@PathVariable String nodeId) {
        Map<String, Object> drilldown = drilldownService.getTopologyDrilldown(nodeId);
        return ApiResponse.success(drilldown);
    }
}
