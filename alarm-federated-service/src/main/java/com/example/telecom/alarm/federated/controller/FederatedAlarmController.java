package com.example.telecom.alarm.federated.controller;

import com.example.telecom.alarm.federated.*;
import com.example.telecom.alarm.federated.mapper.FederatedAlarmMapper;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.alarm.federated.service.AlarmIngestionService;
import com.example.telecom.alarm.federated.service.FederatedAlarmAggregationService;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/federated/alarms")
public class FederatedAlarmController {

    private final FederatedAlarmRepository alarmRepository;
    private final AlarmIngestionService ingestionService;
    private final FederatedAlarmAggregationService aggregationService;
    private final FederatedAlarmMapper mapper;

    public FederatedAlarmController(FederatedAlarmRepository alarmRepository,
                                     AlarmIngestionService ingestionService,
                                     FederatedAlarmAggregationService aggregationService,
                                     FederatedAlarmMapper mapper) {
        this.alarmRepository = alarmRepository;
        this.ingestionService = ingestionService;
        this.aggregationService = aggregationService;
        this.mapper = mapper;
    }

    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<FederatedAlarmResponse>> ingest(
            @Valid @RequestBody FederatedAlarmRequest request) {
        FederatedAlarmRecord record = ingestionService.ingest(request);
        FederatedAlarmResponse response = mapper.toResponse(record);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{alarmId}")
    public ResponseEntity<ApiResponse<FederatedAlarmResponse>> getById(@PathVariable String alarmId) {
        Optional<FederatedAlarmRecord> recordOpt = alarmRepository.findById(alarmId);
        if (recordOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Alarm not found: " + alarmId));
        }
        FederatedAlarmResponse response = mapper.toResponse(recordOpt.get());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FederatedAlarmResponse>>> list(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) FederatedAlarmStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<FederatedAlarmRecord> records = alarmRepository.findAll();

        if (source != null && !source.isBlank()) {
            records = records.stream()
                    .filter(r -> source.equals(r.getSourceId()))
                    .collect(Collectors.toList());
        }
        if (severity != null) {
            records = records.stream()
                    .filter(r -> severity == r.getSeverity())
                    .collect(Collectors.toList());
        }
        if (status != null) {
            records = records.stream()
                    .filter(r -> status == r.getStatus())
                    .collect(Collectors.toList());
        }
        if (from != null) {
            records = records.stream()
                    .filter(r -> !r.getAlarmTime().isBefore(from))
                    .collect(Collectors.toList());
        }
        if (to != null) {
            records = records.stream()
                    .filter(r -> !r.getAlarmTime().isAfter(to))
                    .collect(Collectors.toList());
        }

        List<FederatedAlarmResponse> responses = records.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<FederatedAlarmSummary>> getSummary() {
        FederatedAlarmSummary summary = aggregationService.getAggregationSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/region/{regionCode}")
    public ResponseEntity<ApiResponse<List<FederatedAlarmResponse>>> getByRegion(
            @PathVariable String regionCode) {
        List<FederatedAlarmRecord> records = alarmRepository.findByRegion(regionCode);
        List<FederatedAlarmResponse> responses = records.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
