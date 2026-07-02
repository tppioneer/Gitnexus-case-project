package com.example.telecom.collector.repository;

import com.example.telecom.common.device.DeviceMetric;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceMetricRepository {
    private final Map<String, DeviceMetric> metrics = new ConcurrentHashMap<>();

    public DeviceMetric save(DeviceMetric metric) {
        metrics.put(metric.getMetricId(), metric);
        return metric;
    }

    public Optional<DeviceMetric> findById(String metricId) {
        return Optional.ofNullable(metrics.get(metricId));
    }

    public List<DeviceMetric> findByDeviceId(String deviceId) {
        return metrics.values().stream()
                .filter(m -> deviceId.equals(m.getDeviceId()))
                .toList();
    }

    public List<DeviceMetric> findAll() {
        return new ArrayList<>(metrics.values());
    }
}
