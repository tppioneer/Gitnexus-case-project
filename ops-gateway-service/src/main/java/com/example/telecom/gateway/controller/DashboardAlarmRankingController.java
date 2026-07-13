package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.service.DashboardAlarmRankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/rankings")
public class DashboardAlarmRankingController {

    private final DashboardAlarmRankingService alarmRankingService;

    public DashboardAlarmRankingController(DashboardAlarmRankingService alarmRankingService) {
        this.alarmRankingService = alarmRankingService;
    }

    @GetMapping("/alarms")
    public ApiResponse<List<Map<String, Object>>> getAlarmRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> ranking = alarmRankingService.getAlarmRanking(limit);
        return ApiResponse.success(ranking);
    }

    @GetMapping("/devices")
    public ApiResponse<List<Map<String, Object>>> getDeviceRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> ranking = alarmRankingService.getDeviceRanking(limit);
        return ApiResponse.success(ranking);
    }

    @GetMapping("/regions")
    public ApiResponse<List<Map<String, Object>>> getRegionRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> ranking = alarmRankingService.getRegionRanking(limit);
        return ApiResponse.success(ranking);
    }
}
