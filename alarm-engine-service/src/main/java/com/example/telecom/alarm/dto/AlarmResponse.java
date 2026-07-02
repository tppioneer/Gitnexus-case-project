package com.example.telecom.alarm.dto;

public class AlarmResponse {
    private String alarmId;
    private String deviceId;
    private String metricType;
    private String severity;
    private String status;
    private String description;
    private String deviceRegionCode;
    private long createdTime;
    private long updatedTime;

    public String getAlarmId() { return alarmId; }
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeviceRegionCode() { return deviceRegionCode; }
    public void setDeviceRegionCode(String deviceRegionCode) { this.deviceRegionCode = deviceRegionCode; }
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    public long getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(long updatedTime) { this.updatedTime = updatedTime; }
}
