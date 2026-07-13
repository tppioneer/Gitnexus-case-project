package com.example.telecom.gateway.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.gateway.service.DashboardTopologyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/topology")
public class DashboardTopologyController {

    private final DashboardTopologyService topologyService;

    public DashboardTopologyController(DashboardTopologyService topologyService) {
        this.topologyService = topologyService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getTopology() {
        Map<String, Object> topology = topologyService.getTopology();
        return ApiResponse.success(topology);
    }

    @GetMapping("/node/{nodeId}")
    public ApiResponse<Map<String, Object>> getNodeTopology(@PathVariable String nodeId) {
        Map<String, Object> nodeTopology = topologyService.getNodeTopology(nodeId);
        return ApiResponse.success(nodeTopology);
    }

    @GetMapping("/region/{regionCode}")
    public ApiResponse<Map<String, Object>> getRegionTopology(@PathVariable String regionCode) {
        Map<String, Object> regionTopology = topologyService.getRegionTopology(regionCode);
        return ApiResponse.success(regionTopology);
    }
}
