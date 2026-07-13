package com.example.telecom.alarm.controller;

import com.example.telecom.alarm.dto.AlarmAnalyticsRequest;
import com.example.telecom.alarm.dto.AlarmAnalyticsResponse;
import com.example.telecom.alarm.service.AlarmTrendAnalysisService;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for alarm analytics endpoints.
 * Provides trend analysis, distribution, top alarm types, and data export.
 */
@RestController
@RequestMapping("/api/alarms/analytics")
public class AlarmAnalyticsController {

    private final AlarmTrendAnalysisService trendAnalysisService;

    public AlarmAnalyticsController(AlarmTrendAnalysisService trendAnalysisService) {
        this.trendAnalysisService = trendAnalysisService;
    }

    @GetMapping("/trends")
    public ApiResponse<AlarmAnalyticsResponse> getTrends(
            @RequestParam(defaultValue = "24h") String timeRange) {
        Map<String, Object> analysis = trendAnalysisService.analyze(timeRange);

        AlarmAnalyticsResponse response = new AlarmAnalyticsResponse();
        response.setAnalyticsType("trends");
        response.setData(analysis);
        response.setPeriod(timeRange);
        response.setGeneratedTime(System.currentTimeMillis());

        return ApiResponse.success(response);
    }

    @GetMapping("/distribution")
    public ApiResponse<AlarmAnalyticsResponse> getDistribution(
            @RequestParam(defaultValue = "hourly") String type) {
        Map<String, Object> data = new LinkedHashMap<>();

        if ("hourly".equalsIgnoreCase(type)) {
            Map<Integer, Long> hourly = trendAnalysisService.getHourlyDistribution();
            data.put("type", "hourly");
            data.put("distribution", hourly);
            data.put("peakHour", hourly.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> String.valueOf(e.getKey()))
                    .orElse("N/A"));
        } else if ("daily".equalsIgnoreCase(type)) {
            Map<Integer, Long> daily = trendAnalysisService.getDailyDistribution();
            data.put("type", "daily");
            data.put("distribution", daily);
        } else {
            data.put("type", "severity");
            Map<Severity, Long> severity = trendAnalysisService.getSeverityTrend();
            data.put("distribution", severity);
        }

        AlarmAnalyticsResponse response = new AlarmAnalyticsResponse();
        response.setAnalyticsType("distribution-" + type);
        response.setData(data);
        response.setPeriod("all");
        response.setGeneratedTime(System.currentTimeMillis());

        return ApiResponse.success(response);
    }

    @GetMapping("/top-types")
    public ApiResponse<AlarmAnalyticsResponse> getTopAlarmTypes(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map.Entry<String, Long>> topTypes = trendAnalysisService.getTopAlarmTypes(limit);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("limit", limit);
        data.put("topTypes", topTypes.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                )));

        AlarmAnalyticsResponse response = new AlarmAnalyticsResponse();
        response.setAnalyticsType("top-types");
        response.setData(data);
        response.setPeriod("all");
        response.setGeneratedTime(System.currentTimeMillis());

        return ApiResponse.success(response);
    }

    @PostMapping("/export")
    public ApiResponse<AlarmAnalyticsResponse> exportAnalytics(
            @RequestBody AlarmAnalyticsRequest request) {
        Map<String, Object> analysis = trendAnalysisService.analyze(
                request.getTimeRange() != null ? request.getTimeRange() : "24h");

        AlarmAnalyticsResponse response = new AlarmAnalyticsResponse();
        response.setAnalyticsType("export");
        response.setData(analysis);
        response.setPeriod(request.getTimeRange());
        response.setGeneratedTime(System.currentTimeMillis());

        return ApiResponse.success(response);
    }
}
