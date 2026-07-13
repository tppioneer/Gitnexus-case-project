package com.example.telecom.vendor.controller;

import com.example.telecom.vendor.domain.VendorSlaReport;
import com.example.telecom.vendor.dto.VendorSlaReportRequest;
import com.example.telecom.vendor.dto.VendorSlaReportResponse;
import com.example.telecom.vendor.mapper.VendorTicketMapper;
import com.example.telecom.vendor.repository.VendorSlaRepository;
import com.example.telecom.vendor.service.VendorSlaTrackingService;
import com.example.telecom.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendor/sla/reports")
public class VendorSlaReportController {

    private final VendorSlaTrackingService slaTrackingService;
    private final VendorSlaRepository slaRepository;

    public VendorSlaReportController(VendorSlaTrackingService slaTrackingService,
                                     VendorSlaRepository slaRepository) {
        this.slaTrackingService = slaTrackingService;
        this.slaRepository = slaRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorSlaReportResponse>>> listReports(
            @RequestParam(required = false) String vendorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<VendorSlaReport> reports;
        if (vendorId != null) {
            reports = slaRepository.findByVendorId(vendorId);
        } else if (dateFrom != null && dateTo != null) {
            reports = slaRepository.findByDateRange(dateFrom, dateTo);
        } else {
            reports = slaRepository.findAll();
        }
        List<VendorSlaReportResponse> responses = reports.stream()
                .map(VendorTicketMapper::toSlaReportResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<VendorSlaReportResponse>> getReport(
            @PathVariable String reportId) {
        Optional<VendorSlaReportResponse> response = slaRepository.findById(reportId)
                .map(VendorTicketMapper::toSlaReportResponse);
        return response
                .map(r -> ResponseEntity.ok(ApiResponse.success(r)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Report not found: " + reportId)));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<VendorSlaReportResponse>> generateReport(
            @Valid @RequestBody VendorSlaReportRequest request) {
        VendorSlaReport report = slaTrackingService.getVendorSlaReport(
                request.getVendorId(), request.getPeriodFrom(), request.getPeriodTo());
        VendorSlaReportResponse response = VendorTicketMapper.toSlaReportResponse(report);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(
            @RequestParam String vendorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        VendorSlaReport report = slaTrackingService.getVendorSlaReport(vendorId, dateFrom, dateTo);
        VendorSlaReportResponse response = VendorTicketMapper.toSlaReportResponse(report);
        double slaPercentage = slaTrackingService.calculateSlaPercentage(vendorId, dateFrom, dateTo);
        Map<String, Object> summary = slaTrackingService.getSlaSummary(vendorId);
        summary.put("currentReport", response);
        summary.put("periodSlaPercentage", Math.round(slaPercentage * 100.0) / 100.0);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/latest/{vendorId}")
    public ResponseEntity<ApiResponse<VendorSlaReportResponse>> getLatestReport(
            @PathVariable String vendorId) {
        return slaRepository.findLatest(vendorId)
                .map(r -> ResponseEntity.ok(ApiResponse.success(VendorTicketMapper.toSlaReportResponse(r))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "No reports found for vendor: " + vendorId)));
    }
}
