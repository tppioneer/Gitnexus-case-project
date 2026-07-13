package com.example.telecom.device.lifecycle.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeviceLifecycleEvent {
    private String eventId;
    private String deviceId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime timestamp;
    private String regionCode;
    private String performedBy;

    public DeviceLifecycleEvent() {}

    public DeviceLifecycleEvent(String deviceId, String previousStatus, String newStatus,
                                 String regionCode, String performedBy) {
        this.eventId = UUID.randomUUID().toString();
        this.deviceId = deviceId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.timestamp = LocalDateTime.now();
        this.regionCode = regionCode;
        this.performedBy = performedBy;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
}
