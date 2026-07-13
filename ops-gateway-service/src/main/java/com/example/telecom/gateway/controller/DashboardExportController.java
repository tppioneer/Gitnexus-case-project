package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.dto.DashboardExportRequest;
import com.example.telecom.gateway.dto.DashboardExportResponse;
import com.example.telecom.gateway.service.DashboardExportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard/exports")
public class DashboardExportController {

    private final DashboardExportService exportService;

    public DashboardExportController(DashboardExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping
    public ApiResponse<DashboardExportResponse> createExport(@RequestBody DashboardExportRequest request) {
        DashboardExportResponse response = exportService.createExport(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{exportId}")
    public ApiResponse<DashboardExportResponse> getExportStatus(@PathVariable String exportId) {
        DashboardExportResponse response = exportService.getExportStatus(exportId);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<DashboardExportResponse>> listExports() {
        List<DashboardExportResponse> exports = exportService.listExports();
        return ApiResponse.success(exports);
    }

    @PostMapping("/{exportId}/download")
    public ApiResponse<DashboardExportResponse> downloadExport(@PathVariable String exportId) {
        DashboardExportResponse response = exportService.getExportStatus(exportId);
        return ApiResponse.success(response);
    }
}
