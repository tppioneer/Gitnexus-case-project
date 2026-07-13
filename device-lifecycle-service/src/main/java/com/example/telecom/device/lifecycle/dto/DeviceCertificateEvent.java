package com.example.telecom.device.lifecycle.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeviceCertificateEvent {
    private String eventId;
    private String deviceId;
    private String certificateId;
    private String eventType;
    private LocalDateTime timestamp;
    private String message;

    public DeviceCertificateEvent() {}

    public DeviceCertificateEvent(String deviceId, String certificateId, String eventType, String message) {
        this.eventId = UUID.randomUUID().toString();
        this.deviceId = deviceId;
        this.certificateId = certificateId;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
