package com.example.telecom.collector.mapper;

import java.util.UUID;

import com.example.telecom.collector.dto.DeviceMetricResponse;
import com.example.telecom.collector.dto.MetricIngestRequest;
import com.example.telecom.common.device.*;

/**
 * Maps between request/domain/response. EXPLICITLY maps maintenanceRegionCode → deviceRegionCode.
 */
public class DeviceMetricMapper {

    public RawDeviceMetric toRawMetric(String deviceId, MetricIngestRequest request) {
        return new RawDeviceMetric(
                deviceId,
                request.getVendor(),
                request.getRawMetricName(),
                request.getRawValue(),
                request.getRawUnit(),
                request.getTimestamp()
        );
    }

    /**
     * Creates a DeviceMetricEvent with EXPLICIT field mapping:
     * DeviceInfo.maintenanceRegionCode → DeviceMetricEvent.deviceRegionCode
     * This is a critical mapping for Case C field propagation.
     */
    public DeviceMetricEvent toEvent(DeviceMetric metric, DeviceInfo deviceInfo) {
        return new DeviceMetricEvent(
                UUID.randomUUID().toString(),
                metric.getDeviceId(),
                metric.getMetricId(),
                metric.getMetricType().name(),
                metric.getValue(),
                metric.getUnit(),
                System.currentTimeMillis(),
                deviceInfo.getMaintenanceRegionCode()   // ← EXPLICIT: DeviceInfo.maintenanceRegionCode → event.deviceRegionCode
        );
    }

    public DeviceMetricResponse toResponse(DeviceMetric metric) {
        DeviceMetricResponse response = new DeviceMetricResponse();
        response.setMetricId(metric.getMetricId());
        response.setDeviceId(metric.getDeviceId());
        response.setMetricType(metric.getMetricType().name());
        response.setValue(metric.getValue());
        response.setUnit(metric.getUnit());
        response.setCollectedAt(metric.getCollectedAt().toEpochMilli());
        response.setNormalized(metric.isNormalized());
        return response;
    }
}
