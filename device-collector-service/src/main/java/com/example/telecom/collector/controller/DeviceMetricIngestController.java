package com.example.telecom.collector.controller;

import com.example.telecom.collector.dto.DeviceMetricResponse;
import com.example.telecom.collector.dto.MetricIngestRequest;
import com.example.telecom.collector.mapper.DeviceMetricMapper;
import com.example.telecom.collector.service.CollectorAuditService;
import com.example.telecom.collector.service.DeviceRegistryService;
import com.example.telecom.collector.service.MetricCollectorService;
import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.device.*;
import org.springframework.web.bind.annotation.*;

/**
 * Entry point for device metric ingestion.
 * POST /api/devices/{deviceId}/metrics
 * This is the START of Case A flow.
 */
@RestController
@RequestMapping("/api/devices")
public class DeviceMetricIngestController {

    private final MetricCollectorService metricCollectorService;
    private final DeviceRegistryService deviceRegistryService;
    private final DeviceMetricMapper deviceMetricMapper;
    private final CollectorAuditService collectorAuditService;

    public DeviceMetricIngestController(MetricCollectorService metricCollectorService,
                                         DeviceRegistryService deviceRegistryService,
                                         DeviceMetricMapper deviceMetricMapper,
                                         CollectorAuditService collectorAuditService) {
        this.metricCollectorService = metricCollectorService;
        this.deviceRegistryService = deviceRegistryService;
        this.deviceMetricMapper = deviceMetricMapper;
        this.collectorAuditService = collectorAuditService;
    }

    @PostMapping("/{deviceId}/metrics")
    public ApiResponse<DeviceMetricResponse> ingestMetric(@PathVariable String deviceId,
                                                           @RequestBody MetricIngestRequest request) {
        DeviceInfo deviceInfo = deviceRegistryService.findActiveDevice(deviceId);
        RawDeviceMetric rawMetric = deviceMetricMapper.toRawMetric(deviceId, request);
        DeviceMetric metric = metricCollectorService.acceptMetric(rawMetric, deviceInfo);
        DeviceMetricResponse response = deviceMetricMapper.toResponse(metric);
        collectorAuditService.logMetricIngested(deviceId, metric.getMetricId(), "system");
        return ApiResponse.success(response);
    }
}
