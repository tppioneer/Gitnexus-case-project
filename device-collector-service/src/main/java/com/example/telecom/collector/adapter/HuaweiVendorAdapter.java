package com.example.telecom.collector.adapter;

import com.example.telecom.common.device.*;

import java.time.Instant;
import java.util.UUID;

public class HuaweiVendorAdapter implements VendorAdapter {

    @Override
    public DeviceMetric normalizeRawMetric(RawDeviceMetric rawMetric, DeviceInfo deviceInfo) {
        MetricType metricType = mapMetricType(rawMetric.getRawMetricName());
        double normalizedValue = rawMetric.getRawValue();

        // Huawei reports CPU as percentage*100, normalize to percentage
        if ("cpu_usage_ratio".equals(rawMetric.getRawMetricName())) {
            normalizedValue = rawMetric.getRawValue() / 100.0;
        }
        // Huawei reports optical power in 0.01 dBm, normalize to dBm
        if ("optical_rx_power".equals(rawMetric.getRawMetricName())) {
            normalizedValue = rawMetric.getRawValue() / 100.0;
        }

        return new DeviceMetric(
                UUID.randomUUID().toString(),
                rawMetric.getDeviceId(),
                metricType,
                normalizedValue,
                resolveUnit(metricType),
                Instant.ofEpochMilli(rawMetric.getTimestamp()),
                "Huawei"
        );
    }

    private MetricType mapMetricType(String rawName) {
        return switch (rawName) {
            case "cpu_usage_ratio" -> MetricType.CPU_USAGE;
            case "mem_usage_ratio" -> MetricType.MEMORY_USAGE;
            case "optical_rx_power" -> MetricType.OPTICAL_POWER;
            case "port_packet_loss" -> MetricType.PACKET_LOSS;
            case "board_temperature" -> MetricType.TEMPERATURE;
            default -> throw new IllegalArgumentException("Unknown Huawei metric: " + rawName);
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
