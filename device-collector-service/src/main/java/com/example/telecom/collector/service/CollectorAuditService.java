package com.example.telecom.collector.service;

import com.example.telecom.common.audit.AuditEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CollectorAuditService {

    private final List<AuditEntry> auditLog = new ArrayList<>();

    public void logMetricIngested(String deviceId, String metricId, String operator) {
        AuditEntry entry = new AuditEntry(
                UUID.randomUUID().toString(),
                "DeviceMetric",
                metricId,
                "INGEST",
                operator,
                "Metric ingested for device " + deviceId,
                System.currentTimeMillis()
        );
        auditLog.add(entry);
    }

    public void logDeviceRegistered(String deviceId, String operator) {
        AuditEntry entry = new AuditEntry(
                UUID.randomUUID().toString(),
                "DeviceInfo",
                deviceId,
                "REGISTER",
                operator,
                "Device registered",
                System.currentTimeMillis()
        );
        auditLog.add(entry);
    }

    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}
