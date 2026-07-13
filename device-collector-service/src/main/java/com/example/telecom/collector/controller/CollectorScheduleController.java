package com.example.telecom.collector.controller;

import com.example.telecom.collector.dto.CollectorScheduleRequest;
import com.example.telecom.collector.dto.CollectorScheduleResponse;
import com.example.telecom.collector.service.CollectorScheduleService;
import com.example.telecom.common.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collector/schedules")
public class CollectorScheduleController {

    private final CollectorScheduleService scheduleService;

    public CollectorScheduleController(CollectorScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ApiResponse<CollectorScheduleResponse> createSchedule(@RequestBody CollectorScheduleRequest request) {
        CollectorScheduleResponse response = scheduleService.createSchedule(request);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<CollectorScheduleResponse>> listSchedules() {
        List<CollectorScheduleResponse> schedules = scheduleService.listSchedules();
        return ApiResponse.success(schedules);
    }

    @GetMapping("/{scheduleId}")
    public ApiResponse<CollectorScheduleResponse> getSchedule(@PathVariable String scheduleId) {
        CollectorScheduleResponse response = scheduleService.getSchedule(scheduleId);
        return ApiResponse.success(response);
    }

    @PutMapping("/{scheduleId}")
    public ApiResponse<CollectorScheduleResponse> updateSchedule(@PathVariable String scheduleId,
                                                                  @RequestBody CollectorScheduleRequest request) {
        CollectorScheduleResponse response = scheduleService.updateSchedule(scheduleId, request);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(@PathVariable String scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{scheduleId}/start")
    public ApiResponse<CollectorScheduleResponse> startSchedule(@PathVariable String scheduleId) {
        CollectorScheduleResponse response = scheduleService.startSchedule(scheduleId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{scheduleId}/stop")
    public ApiResponse<CollectorScheduleResponse> stopSchedule(@PathVariable String scheduleId) {
        CollectorScheduleResponse response = scheduleService.stopSchedule(scheduleId);
        return ApiResponse.success(response);
    }
}
