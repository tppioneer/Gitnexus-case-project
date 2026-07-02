package com.example.telecom.collector.adapter;

import com.example.telecom.common.device.*;

import java.time.Instant;
import java.util.UUID;

public class GenericSnmpVendorAdapter implements VendorAdapter {

    @Override
    public DeviceMetric normalizeRawMetric(RawDeviceMetric rawMetric, DeviceInfo deviceInfo) {
        MetricType metricType = mapMetricType(rawMetric.getRawMetricName());

        return new DeviceMetric(
                UUID.randomUUID().toString(),
                rawMetric.getDeviceId(),
                metricType,
                rawMetric.getRawValue(),
                resolveUnit(metricType),
                Instant.ofEpochMilli(rawMetric.getTimestamp()),
                "Generic"
        );
    }

    private MetricType mapMetricType(String rawName) {
        return switch (rawName) {
            case "snmp_cpu" -> MetricType.CPU_USAGE;
            case "snmp_memory" -> MetricType.MEMORY_USAGE;
            case "snmp_optical" -> MetricType.OPTICAL_POWER;
            case "snmp_loss" -> MetricType.PACKET_LOSS;
            case "snmp_temp" -> MetricType.TEMPERATURE;
            default -> throw new IllegalArgumentException("Unknown SNMP metric: " + rawName);
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
