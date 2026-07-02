package com.example.telecom.alarm.controller;

import com.example.telecom.alarm.dto.AlarmExportRequest;
import com.example.telecom.alarm.dto.AlarmExportResponse;
import com.example.telecom.alarm.service.AlarmExportService;
import com.example.telecom.common.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alarms")
public class AlarmExportController {

    private final AlarmExportService alarmExportService;

    public AlarmExportController(AlarmExportService alarmExportService) {
        this.alarmExportService = alarmExportService;
    }

    @PostMapping("/export")
    public ApiResponse<AlarmExportResponse> exportAlarms(@RequestBody AlarmExportRequest request) {
        return ApiResponse.success(alarmExportService.exportAlarms(request));
    }
}
