package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.dto.DashboardTrendResponse;
import com.example.telecom.gateway.service.DashboardTrendService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard/trends")
public class DashboardTrendController {

    private final DashboardTrendService trendService;

    public DashboardTrendController(DashboardTrendService trendService) {
        this.trendService = trendService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardTrendResponse> getOverviewTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        DashboardTrendResponse trend = trendService.getOverviewTrend(from, to);
        return ApiResponse.success(trend);
    }

    @GetMapping("/devices")
    public ApiResponse<DashboardTrendResponse> getDeviceTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        DashboardTrendResponse trend = trendService.getDeviceTrend(from, to);
        return ApiResponse.success(trend);
    }

    @GetMapping("/alarms")
    public ApiResponse<DashboardTrendResponse> getAlarmTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        DashboardTrendResponse trend = trendService.getAlarmTrend(from, to);
        return ApiResponse.success(trend);
    }

    @GetMapping("/workorders")
    public ApiResponse<DashboardTrendResponse> getWorkOrderTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        DashboardTrendResponse trend = trendService.getWorkOrderTrend(from, to);
        return ApiResponse.success(trend);
    }
}
