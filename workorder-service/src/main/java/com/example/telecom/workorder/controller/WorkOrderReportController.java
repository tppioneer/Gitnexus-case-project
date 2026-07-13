package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.workorder.dto.WorkOrderReportRequest;
import com.example.telecom.workorder.dto.WorkOrderReportResponse;
import com.example.telecom.workorder.service.WorkOrderReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workorders/reports")
public class WorkOrderReportController {

    private final WorkOrderReportService workOrderReportService;

    public WorkOrderReportController(WorkOrderReportService workOrderReportService) {
        this.workOrderReportService = workOrderReportService;
    }

    @PostMapping
    public ApiResponse<WorkOrderReportResponse> generateReport(@RequestBody WorkOrderReportRequest request) {
        return ApiResponse.success(workOrderReportService.generateReport(request));
    }

    @GetMapping("/summary")
    public ApiResponse<WorkOrderReportResponse> getSummary() {
        return ApiResponse.success(workOrderReportService.generateSummaryReport());
    }

    @GetMapping("/{reportId}")
    public ApiResponse<WorkOrderReportResponse> getReport(@PathVariable String reportId) {
        return workOrderReportService.getReportById(reportId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Report not found"));
    }

    @PostMapping("/export")
    public ApiResponse<String> exportReport(@RequestBody WorkOrderReportRequest request) {
        return ApiResponse.success(workOrderReportService.exportCsv(request));
    }
}
