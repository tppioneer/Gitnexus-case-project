package com.example.telecom.alarm.controller;

import com.example.telecom.alarm.dto.AlarmQueryRequest;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.service.AlarmAuditService;
import com.example.telecom.alarm.service.AlarmLifecycleService;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.api.PagedResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {

    private final AlarmRepository alarmRepository;
    private final AlarmLifecycleService alarmLifecycleService;
    private final AlarmAuditService alarmAuditService;

    public AlarmController(AlarmRepository alarmRepository,
                            AlarmLifecycleService alarmLifecycleService,
                            AlarmAuditService alarmAuditService) {
        this.alarmRepository = alarmRepository;
        this.alarmLifecycleService = alarmLifecycleService;
        this.alarmAuditService = alarmAuditService;
    }

    @GetMapping
    public ApiResponse<PagedResult<AlarmRecord>> queryAlarms(AlarmQueryRequest request) {
        List<AlarmRecord> all = alarmRepository.findAll();
        // Filter by deviceId
        if (request.getDeviceId() != null) {
            all = all.stream().filter(a -> request.getDeviceId().equals(a.getDeviceId()))
                    .collect(Collectors.toList());
        }
        // Paginate
        int total = all.size();
        int from = (request.getPage() - 1) * request.getPageSize();
        int to = Math.min(from + request.getPageSize(), total);
        List<AlarmRecord> page = from < total ? all.subList(from, to) : List.of();
        return ApiResponse.success(new PagedResult<>(page, request.getPage(), request.getPageSize(), total));
    }

    @GetMapping("/{alarmId}")
    public ApiResponse<AlarmRecord> getAlarm(@PathVariable String alarmId) {
        return alarmRepository.findById(alarmId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Alarm not found"));
    }

    @PostMapping("/{alarmId}/acknowledge")
    public ApiResponse<AlarmRecord> acknowledge(@PathVariable String alarmId) {
        AlarmRecord alarm = alarmLifecycleService.acknowledge(alarmId);
        alarmAuditService.logAlarmAcknowledged(alarmId, "operator");
        return ApiResponse.success(alarm);
    }

    @PostMapping("/{alarmId}/clear")
    public ApiResponse<AlarmRecord> clear(@PathVariable String alarmId) {
        AlarmRecord alarm = alarmLifecycleService.clear(alarmId);
        return ApiResponse.success(alarm);
    }
}
