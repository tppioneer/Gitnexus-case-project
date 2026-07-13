package com.example.telecom.sla.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.sla.SlaReportRequest;
import com.example.telecom.sla.SlaReportResponse;
import com.example.telecom.sla.domain.SlaReport;
import com.example.telecom.sla.mapper.SlaMapper;
import com.example.telecom.sla.service.SlaReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sla/reports")
public class SlaReportController {

    private static final Logger log = LoggerFactory.getLogger(SlaReportController.class);

    private final SlaReportService reportService;
    private final SlaMapper mapper;

    public SlaReportController(SlaReportService reportService, SlaMapper mapper) {
        this.reportService = reportService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SlaReportResponse>>> list(
            @RequestParam(required = false) String contractId) {
        log.info("GET /api/sla/reports - contractId={}", contractId);
        List<SlaReport> reports = reportService.listReports(contractId);
        List<SlaReportResponse> responses = reports.stream()
                .map(mapper::toReportResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<SlaReportResponse>> get(@PathVariable String reportId) {
        log.info("GET /api/sla/reports/{}", reportId);
        try {
            SlaReport report = reportService.getReport(reportId);
            return ResponseEntity.ok(ApiResponse.success(mapper.toReportResponse(report)));
        } catch (DomainException e) {
            log.warn("Report not found: {}", reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<SlaReportResponse>> generate(@RequestBody SlaReportRequest request) {
        log.info("POST /api/sla/reports/generate - contractId={}, type={}",
                request.getContractId(), request.getReportType());
        try {
            SlaReport report = reportService.generateReport(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(mapper.toReportResponse(report)));
        } catch (DomainException e) {
            log.warn("Error generating report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @GetMapping("/export/{reportId}")
    public ResponseEntity<byte[]> export(@PathVariable String reportId,
                                          @RequestParam(defaultValue = "PDF") String format) {
        log.info("GET /api/sla/reports/export/{} - format={}", reportId, format);
        try {
            byte[] content = reportService.exportReport(reportId, format);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "report-" + reportId + "." + format.toLowerCase());
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (DomainException e) {
            log.warn("Report not found for export: {}", reportId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/schedule")
    public ResponseEntity<ApiResponse<String>> schedule(
            @RequestParam String contractId,
            @RequestParam String cronExpression) {
        log.info("POST /api/sla/reports/schedule - contractId={}, cron={}", contractId, cronExpression);
        try {
            String scheduleId = reportService.scheduleReport(contractId, cronExpression);
            return ResponseEntity.ok(ApiResponse.success(scheduleId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
