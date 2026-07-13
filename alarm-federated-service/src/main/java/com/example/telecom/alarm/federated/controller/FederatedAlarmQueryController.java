package com.example.telecom.alarm.federated.controller;

import com.example.telecom.alarm.federated.*;
import com.example.telecom.alarm.federated.mapper.FederatedAlarmMapper;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.alarm.federated.service.FederatedAlarmAggregationService;
import com.example.telecom.alarm.federated.aggregation.AggregationResult;
import com.example.telecom.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/federated/alarms/query")
public class FederatedAlarmQueryController {

    private final FederatedAlarmRepository alarmRepository;
    private final FederatedAlarmAggregationService aggregationService;
    private final FederatedAlarmMapper mapper;

    public FederatedAlarmQueryController(FederatedAlarmRepository alarmRepository,
                                          FederatedAlarmAggregationService aggregationService,
                                          FederatedAlarmMapper mapper) {
        this.alarmRepository = alarmRepository;
        this.aggregationService = aggregationService;
        this.mapper = mapper;
    }

    @GetMapping("/cross-region")
    public ResponseEntity<ApiResponse<List<CrossRegionAlarmView>>> getCrossRegionView() {
        List<FederatedAlarmRecord> allAlarms = alarmRepository.findAll();
        Map<String, List<FederatedAlarmRecord>> byRegion = allAlarms.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRegionCode() != null ? r.getRegionCode() : "UNKNOWN"));

        List<CrossRegionAlarmView> views = new ArrayList<>();
        List<String> regions = new ArrayList<>(byRegion.keySet());

        for (int i = 0; i < regions.size(); i++) {
            for (int j = i + 1; j < regions.size(); j++) {
                String sourceRegion = regions.get(i);
                String targetRegion = regions.get(j);
                List<FederatedAlarmRecord> sourceAlarms = byRegion.get(sourceRegion);
                for (FederatedAlarmRecord alarm : sourceAlarms) {
                    CrossRegionAlarmView view = mapper.toCrossRegionView(alarm);
                    view.setTargetRegionCode(targetRegion);
                    view.setHops(1);
                    views.add(view);
                }
            }
        }

        return ResponseEntity.ok(ApiResponse.success(views));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<FederatedAlarmRecord> allAlarms = alarmRepository.findAll();

        stats.put("totalAlarms", allAlarms.size());

        Map<String, Long> bySeverity = allAlarms.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getSeverity() != null ? a.getSeverity().name() : "UNKNOWN",
                        Collectors.counting()));
        stats.put("bySeverity", bySeverity);

        Map<String, Long> byStatus = allAlarms.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus() != null ? a.getStatus().name() : "UNKNOWN",
                        Collectors.counting()));
        stats.put("byStatus", byStatus);

        Map<String, Long> byRegion = allAlarms.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getRegionCode() != null ? a.getRegionCode() : "UNKNOWN",
                        Collectors.counting()));
        stats.put("byRegion", byRegion);

        long correlatedCount = allAlarms.stream()
                .filter(a -> a.getCorrelationGroupId() != null)
                .count();
        stats.put("correlatedAlarms", correlatedCount);

        long escalatedCount = allAlarms.stream()
                .filter(a -> a.getEscalationLevel() > 0)
                .count();
        stats.put("escalatedAlarms", escalatedCount);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/timeline")
    public ResponseEntity<ApiResponse<AggregationResult>> getTimeline(
            @RequestParam(name = "hours", defaultValue = "24") int hours) {
        LocalDateTime from = LocalDateTime.now().minusHours(hours);
        List<FederatedAlarmRecord> recentAlarms = alarmRepository.findAll().stream()
                .filter(r -> !r.getAlarmTime().isBefore(from))
                .collect(Collectors.toList());

        Duration window = Duration.ofMinutes(Math.max(1, hours * 60L / 24));
        AggregationResult result = aggregationService.aggregateByTimeWindow(recentAlarms, window);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/by-source/{sourceId}")
    public ResponseEntity<ApiResponse<List<FederatedAlarmResponse>>> getBySource(
            @PathVariable String sourceId) {
        List<FederatedAlarmRecord> records = alarmRepository.findBySource(sourceId);
        List<FederatedAlarmResponse> responses = records.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
