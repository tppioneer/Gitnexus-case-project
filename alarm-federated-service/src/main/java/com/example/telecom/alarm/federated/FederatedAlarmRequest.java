package com.example.telecom.alarm.federated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FederatedAlarmRequest {

    @NotBlank(message = "Source ID is required")
    private String sourceId;

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    @NotBlank(message = "Alarm type is required")
    private String alarmType;

    @NotBlank(message = "Severity is required")
    private String severity;

    @NotNull(message = "Alarm time is required")
    private LocalDateTime alarmTime;

    private String description;

    @NotBlank(message = "Region code is required")
    private String regionCode;

    private String rawMessage;

    public FederatedAlarmRequest() {
    }

    public FederatedAlarmRequest(String sourceId, String deviceId, String alarmType, String severity,
                                  LocalDateTime alarmTime, String description, String regionCode, String rawMessage) {
        this.sourceId = sourceId;
        this.deviceId = deviceId;
        this.alarmType = alarmType;
        this.severity = severity;
        this.alarmTime = alarmTime;
        this.description = description;
        this.regionCode = regionCode;
        this.rawMessage = rawMessage;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(LocalDateTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }
}
