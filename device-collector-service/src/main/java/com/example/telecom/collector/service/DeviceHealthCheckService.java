package com.example.telecom.collector.service;

import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.common.device.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that evaluates device health based on recent metrics.
 * Provides health status calculation for individual devices and regions.
 */
public class DeviceHealthCheckService {

    private final DeviceRegistryRepository deviceRegistryRepository;
    private final DeviceMetricRepository deviceMetricRepository;

    public DeviceHealthCheckService(DeviceRegistryRepository deviceRegistryRepository,
                                     DeviceMetricRepository deviceMetricRepository) {
        this.deviceRegistryRepository = deviceRegistryRepository;
        this.deviceMetricRepository = deviceMetricRepository;
    }

    public enum HealthStatus {
        HEALTHY, DEGRADED, UNHEALTHY, UNKNOWN
    }

    public HealthStatus evaluateDeviceHealth(String deviceId) {
        Optional<DeviceInfo> deviceOpt = deviceRegistryRepository.findActiveDevice(deviceId);
        if (deviceOpt.isEmpty()) return HealthStatus.UNKNOWN;

        List<DeviceMetric> metrics = deviceMetricRepository.findByDeviceId(deviceId);
        if (metrics.isEmpty()) return HealthStatus.UNKNOWN;

        // Check recent metrics for anomalies
        List<DeviceMetric> recentMetrics = metrics.stream()
                .filter(m -> m.isNormalized())
                .toList();

        if (recentMetrics.isEmpty()) return HealthStatus.UNKNOWN;

        int unhealthyCount = 0;
        for (DeviceMetric metric : recentMetrics) {
            if (isMetricAnomalous(metric)) {
                unhealthyCount++;
            }
        }

        double unhealthyRatio = (double) unhealthyCount / recentMetrics.size();
        if (unhealthyRatio > 0.5) return HealthStatus.UNHEALTHY;
        if (unhealthyRatio > 0.2) return HealthStatus.DEGRADED;
        return HealthStatus.HEALTHY;
    }

    private boolean isMetricAnomalous(DeviceMetric metric) {
        return switch (metric.getMetricType()) {
            case CPU_USAGE -> metric.getValue() > 90.0;
            case MEMORY_USAGE -> metric.getValue() > 95.0;
            case PACKET_LOSS -> metric.getValue() > 5.0;
            case OPTICAL_POWER -> metric.getValue() < -30.0;
            case TEMPERATURE -> metric.getValue() > 75.0;
        };
    }

    public Map<String, HealthStatus> evaluateRegionHealth(String regionCode) {
        List<DeviceInfo> regionDevices = deviceRegistryRepository.findByRegionCode(regionCode);
        Map<String, HealthStatus> deviceHealthMap = new LinkedHashMap<>();

        for (DeviceInfo device : regionDevices) {
            if (device.isActive()) {
                HealthStatus status = evaluateDeviceHealth(device.getDeviceId());
                deviceHealthMap.put(device.getDeviceId(), status);
            }
        }
        return deviceHealthMap;
    }

    public Map<String, Long> countHealthByRegion(String regionCode) {
        Map<String, HealthStatus> deviceHealth = evaluateRegionHealth(regionCode);
        Map<String, Long> counts = new HashMap<>();
        counts.put("healthy", deviceHealth.values().stream().filter(s -> s == HealthStatus.HEALTHY).count());
        counts.put("degraded", deviceHealth.values().stream().filter(s -> s == HealthStatus.DEGRADED).count());
        counts.put("unhealthy", deviceHealth.values().stream().filter(s -> s == HealthStatus.UNHEALTHY).count());
        counts.put("unknown", deviceHealth.values().stream().filter(s -> s == HealthStatus.UNKNOWN).count());
        return counts;
    }

    public String generateHealthReport(String regionCode) {
        Map<String, Long> counts = countHealthByRegion(regionCode);
        Map<String, HealthStatus> deviceHealth = evaluateRegionHealth(regionCode);
        long total = deviceHealth.size();
        long healthy = counts.getOrDefault("healthy", 0L);
        double healthRate = total > 0 ? (double) healthy / total * 100.0 : 0.0;

        return String.format(
                "Region %s Health Report: total=%d, healthy=%d (%.1f%%), degraded=%d, unhealthy=%d, unknown=%d",
                regionCode, total, healthy, healthRate,
                counts.getOrDefault("degraded", 0L),
                counts.getOrDefault("unhealthy", 0L),
                counts.getOrDefault("unknown", 0L));
    }
}
