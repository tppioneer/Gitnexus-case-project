package com.example.telecom.sla.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.sla.domain.SlaBreach;
import com.example.telecom.sla.monitor.SlaMonitor;
import com.example.telecom.sla.service.SlaBreachDetectionService;
import com.example.telecom.sla.service.SlaMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sla/monitor")
public class SlaMonitorController {

    private static final Logger log = LoggerFactory.getLogger(SlaMonitorController.class);

    private final SlaMonitoringService monitoringService;
    private final SlaBreachDetectionService breachDetectionService;

    public SlaMonitorController(SlaMonitoringService monitoringService,
                                SlaBreachDetectionService breachDetectionService) {
        this.monitoringService = monitoringService;
        this.breachDetectionService = breachDetectionService;
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentStatus() {
        log.info("GET /api/sla/monitor/status");
        Map<String, Object> summary = monitoringService.getMonitoringSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getContractStatus(
            @PathVariable String contractId) {
        log.info("GET /api/sla/monitor/contract/{}", contractId);
        try {
            Map<String, Object> status = monitoringService.getContractStatus(contractId);
            return ResponseEntity.ok(ApiResponse.success(status));
        } catch (DomainException e) {
            log.warn("Contract not found for status: {}", contractId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @GetMapping("/breaches")
    public ResponseEntity<ApiResponse<List<SlaBreach>>> getBreaches(
            @RequestParam(required = false) String contractId) {
        log.info("GET /api/sla/monitor/breaches - contractId={}", contractId);
        List<SlaBreach> breaches;
        if (contractId != null && !contractId.isBlank()) {
            breaches = breachDetectionService.getBreachHistory(contractId);
        } else {
            breaches = breachDetectionService.getActiveBreaches();
        }
        return ResponseEntity.ok(ApiResponse.success(breaches));
    }

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, SlaMonitor.SlaMonitorResult>>> manualCheck() {
        log.info("POST /api/sla/monitor/check - manual check triggered");
        Map<String, SlaMonitor.SlaMonitorResult> results = monitoringService.checkAllContracts();
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PostMapping("/check/{contractId}")
    public ResponseEntity<ApiResponse<SlaMonitor.SlaMonitorResult>> checkContract(
            @PathVariable String contractId) {
        log.info("POST /api/sla/monitor/check/{}", contractId);
        try {
            SlaMonitor.SlaMonitorResult result = monitoringService.monitor(contractId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (DomainException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        log.info("GET /api/sla/monitor/summary");
        Map<String, Object> summary = monitoringService.getMonitoringSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
