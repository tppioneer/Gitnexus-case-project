package com.example.telecom.alarm.federated;

import com.example.telecom.common.alarm.Severity;
import java.time.LocalDateTime;

public class FederatedAlarmResponse {

    private String alarmId;
    private String sourceId;
    private String deviceId;
    private String alarmType;
    private Severity severity;
    private LocalDateTime alarmTime;
    private String regionCode;
    private FederatedAlarmStatus status;
    private String correlationGroupId;
    private String description;

    public FederatedAlarmResponse() {
    }

    public FederatedAlarmResponse(String alarmId, String sourceId, String deviceId, String alarmType,
                                   Severity severity, LocalDateTime alarmTime, String regionCode,
                                   FederatedAlarmStatus status, String correlationGroupId, String description) {
        this.alarmId = alarmId;
        this.sourceId = sourceId;
        this.deviceId = deviceId;
        this.alarmType = alarmType;
        this.severity = severity;
        this.alarmTime = alarmTime;
        this.regionCode = regionCode;
        this.status = status;
        this.correlationGroupId = correlationGroupId;
        this.description = description;
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

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
