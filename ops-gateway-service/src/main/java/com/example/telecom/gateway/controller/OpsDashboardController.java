package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.dto.DashboardResponse;
import com.example.telecom.gateway.service.GatewayAuditService;
import com.example.telecom.gateway.service.OpsQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class OpsDashboardController {

    private final OpsQueryService opsQueryService;
    private final GatewayAuditService gatewayAuditService;

    public OpsDashboardController(OpsQueryService opsQueryService,
                                   GatewayAuditService gatewayAuditService) {
        this.opsQueryService = opsQueryService;
        this.gatewayAuditService = gatewayAuditService;
    }

    @GetMapping("/overview")
    public ApiResponse<DashboardResponse> getDashboard(
            @RequestParam(required = false, defaultValue = "all") String regionCode) {
        gatewayAuditService.logDashboardAccess(regionCode, "/api/dashboard/overview");
        return ApiResponse.success(opsQueryService.buildDashboard(regionCode));
    }

    @GetMapping("/health")
    public ApiResponse<String> getDeviceHealth(@RequestParam(required = false) String regionCode) {
        return ApiResponse.success("Dashboard healthy, region=" + regionCode);
    }
}
