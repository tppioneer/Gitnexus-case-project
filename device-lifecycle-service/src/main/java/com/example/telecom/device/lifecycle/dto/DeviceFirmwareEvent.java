package com.example.telecom.device.lifecycle.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeviceFirmwareEvent {
    private String eventId;
    private String deviceId;
    private String fromVersion;
    private String toVersion;
    private String eventType;
    private LocalDateTime timestamp;

    public DeviceFirmwareEvent() {}

    public DeviceFirmwareEvent(String deviceId, String fromVersion, String toVersion, String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.deviceId = deviceId;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getFromVersion() { return fromVersion; }
    public void setFromVersion(String fromVersion) { this.fromVersion = fromVersion; }
    public String getToVersion() { return toVersion; }
    public void setToVersion(String toVersion) { this.toVersion = toVersion; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
