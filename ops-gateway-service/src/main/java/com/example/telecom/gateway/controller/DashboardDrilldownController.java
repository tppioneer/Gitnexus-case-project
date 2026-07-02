package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.service.DashboardDrilldownService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/regions")
public class DashboardDrilldownController {

    private final DashboardDrilldownService drilldownService;

    public DashboardDrilldownController(DashboardDrilldownService drilldownService) {
        this.drilldownService = drilldownService;
    }

    @GetMapping("/{regionCode}/drilldown")
    public ApiResponse<Map<String, Object>> getDrilldown(@PathVariable String regionCode) {
        return ApiResponse.success(drilldownService.drillDownRegion(regionCode));
    }
}
