package com.example.telecom.collector.adapter;

import com.example.telecom.common.device.*;

import java.time.Instant;
import java.util.UUID;

public class FiberHomeVendorAdapter implements VendorAdapter {

    @Override
    public DeviceMetric normalizeRawMetric(RawDeviceMetric rawMetric, DeviceInfo deviceInfo) {
        MetricType metricType = mapMetricType(rawMetric.getRawMetricName());
        double normalizedValue = rawMetric.getRawValue();

        // FiberHome reports temperature in 0.1°C, normalize to °C
        if ("fh_temperature".equals(rawMetric.getRawMetricName())) {
            normalizedValue = rawMetric.getRawValue() / 10.0;
        }

        return new DeviceMetric(
                UUID.randomUUID().toString(),
                rawMetric.getDeviceId(),
                metricType,
                normalizedValue,
                resolveUnit(metricType),
                Instant.ofEpochMilli(rawMetric.getTimestamp()),
                "FiberHome"
        );
    }

    private MetricType mapMetricType(String rawName) {
        return switch (rawName) {
            case "fh_cpu" -> MetricType.CPU_USAGE;
            case "fh_memory" -> MetricType.MEMORY_USAGE;
            case "fh_optical" -> MetricType.OPTICAL_POWER;
            case "fh_loss" -> MetricType.PACKET_LOSS;
            case "fh_temperature" -> MetricType.TEMPERATURE;
            default -> throw new IllegalArgumentException("Unknown FiberHome metric: " + rawName);
        };
    }

    private String resolveUnit(MetricType metricType) {
        return switch (metricType) {
            case CPU_USAGE, MEMORY_USAGE, PACKET_LOSS -> "%";
            case OPTICAL_POWER -> "dBm";
            case TEMPERATURE -> "°C";
        };
    }
}
