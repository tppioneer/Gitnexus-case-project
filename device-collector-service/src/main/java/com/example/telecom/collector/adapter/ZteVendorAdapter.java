package com.example.telecom.collector.adapter;

import com.example.telecom.common.device.*;

import java.time.Instant;
import java.util.UUID;

public class ZteVendorAdapter implements VendorAdapter {

    @Override
    public DeviceMetric normalizeRawMetric(RawDeviceMetric rawMetric, DeviceInfo deviceInfo) {
        MetricType metricType = mapMetricType(rawMetric.getRawMetricName());
        double normalizedValue = rawMetric.getRawValue();

        // ZTE reports memory in KB, normalize to percentage
        if ("mem_usage_kb".equals(rawMetric.getRawMetricName())) {
            normalizedValue = Math.min(100.0, rawMetric.getRawValue() / 1024.0);
        }

        return new DeviceMetric(
                UUID.randomUUID().toString(),
                rawMetric.getDeviceId(),
                metricType,
                normalizedValue,
                resolveUnit(metricType),
                Instant.ofEpochMilli(rawMetric.getTimestamp()),
                "ZTE"
        );
    }

    private MetricType mapMetricType(String rawName) {
        return switch (rawName) {
            case "cpu_load" -> MetricType.CPU_USAGE;
            case "mem_usage_kb" -> MetricType.MEMORY_USAGE;
            case "rx_optical_power" -> MetricType.OPTICAL_POWER;
            case "pkt_loss_rate" -> MetricType.PACKET_LOSS;
            case "env_temp" -> MetricType.TEMPERATURE;
            default -> throw new IllegalArgumentException("Unknown ZTE metric: " + rawName);
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
