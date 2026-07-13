package com.example.telecom.device.lifecycle.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditEntry {
    private String auditId;
    private String deviceId;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
    private String details;

    public AuditEntry() {}

    public AuditEntry(String deviceId, String action, String performedBy, LocalDateTime timestamp, String details) {
        this.auditId = UUID.randomUUID().toString();
        this.deviceId = deviceId;
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getAuditId() { return auditId; }
    public void setAuditId(String auditId) { this.auditId = auditId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
