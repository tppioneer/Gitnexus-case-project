package com.example.telecom.collector.service;

import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.common.device.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregates device metrics into summaries for dashboard and reporting.
 */
public class MetricAggregationService {

    private final DeviceMetricRepository deviceMetricRepository;

    public MetricAggregationService(DeviceMetricRepository deviceMetricRepository) {
        this.deviceMetricRepository = deviceMetricRepository;
    }

    public DeviceMetricSummary summarizeDeviceMetrics(String deviceId, String regionCode) {
        List<DeviceMetric> metrics = deviceMetricRepository.findByDeviceId(deviceId);
        DeviceMetricSummary summary = new DeviceMetricSummary(deviceId, regionCode, metrics.size());

        Map<String, Double> latestValues = new HashMap<>();
        Map<String, Double> averageValues = new HashMap<>();
        Map<String, Double> maxValues = new HashMap<>();
        Map<String, Double> minValues = new HashMap<>();

        Map<String, List<Double>> valuesByType = groupByMetricType(metrics);

        for (var entry : valuesByType.entrySet()) {
            String type = entry.getKey();
            List<Double> values = entry.getValue();
            if (values.isEmpty()) continue;

            latestValues.put(type, values.get(values.size() - 1));
            averageValues.put(type, values.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            maxValues.put(type, values.stream().mapToDouble(Double::doubleValue).max().orElse(0));
            minValues.put(type, values.stream().mapToDouble(Double::doubleValue).min().orElse(0));
        }

        summary.setLatestValues(latestValues);
        summary.setAverageValues(averageValues);
        summary.setMaxValues(maxValues);
        summary.setMinValues(minValues);

        if (!metrics.isEmpty()) {
            long minTime = metrics.stream().mapToLong(m -> m.getCollectedAt().toEpochMilli()).min().orElse(0);
            long maxTime = metrics.stream().mapToLong(m -> m.getCollectedAt().toEpochMilli()).max().orElse(0);
            summary.setWindowStartMs(minTime);
            summary.setWindowEndMs(maxTime);
        }

        return summary;
    }

    private Map<String, List<Double>> groupByMetricType(List<DeviceMetric> metrics) {
        Map<String, List<Double>> grouped = new HashMap<>();
        for (DeviceMetric metric : metrics) {
            grouped.computeIfAbsent(metric.getMetricType().name(), k -> new ArrayList<>())
                    .add(metric.getValue());
        }
        return grouped;
    }

    public Map<String, DeviceMetricSummary> summarizeAllDevices() {
        Map<String, DeviceMetricSummary> summaries = new HashMap<>();
        Set<String> deviceIds = deviceMetricRepository.findAll().stream()
                .map(DeviceMetric::getDeviceId)
                .collect(Collectors.toSet());

        for (String deviceId : deviceIds) {
            summaries.put(deviceId, summarizeDeviceMetrics(deviceId, "unknown"));
        }
        return summaries;
    }
}
