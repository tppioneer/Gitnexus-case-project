package com.example.telecom.alarm.federated;

import com.example.telecom.common.alarm.Severity;
import java.time.LocalDateTime;
import java.util.UUID;

public class FederatedAlarmRecord {

    private String alarmId;
    private String sourceId;
    private String deviceId;
    private String alarmType;
    private Severity severity;
    private LocalDateTime alarmTime;
    private String description;
    private String regionCode;
    private String rawMessage;
    private FederatedAlarmStatus status;
    private String correlationGroupId;
    private int escalationLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FederatedAlarmRecord() {
        this.alarmId = UUID.randomUUID().toString();
        this.status = FederatedAlarmStatus.NEW;
        this.escalationLevel = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public FederatedAlarmRecord(String sourceId, String deviceId, String alarmType, Severity severity,
                                 LocalDateTime alarmTime, String description, String regionCode, String rawMessage) {
        this();
        this.sourceId = sourceId;
        this.deviceId = deviceId;
        this.alarmType = alarmType;
        this.severity = severity;
        this.alarmTime = alarmTime;
        this.description = description;
        this.regionCode = regionCode;
        this.rawMessage = rawMessage;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
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

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
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

    public FederatedAlarmStatus getStatus() {
        return status;
    }

    public void setStatus(FederatedAlarmStatus status) {
        this.status = status;
    }

    public String getCorrelationGroupId() {
        return correlationGroupId;
    }

    public void setCorrelationGroupId(String correlationGroupId) {
        this.correlationGroupId = correlationGroupId;
    }

    public int getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(int escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
