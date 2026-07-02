package com.example.telecom.common.alarm;

public class AlarmRecord {
    private String alarmId;
    private String deviceId;
    private String metricId;
    private String metricType;
    private Severity severity;
    private AlarmStatus status;
    private String description;

    /** Downstream field: propagated from DeviceMetricEvent.deviceRegionCode. Semantically different name. */
    private String alarmRegionCode;

    private long createdTime;
    private long updatedTime;

    public AlarmRecord() {}

    public AlarmRecord(String alarmId, String deviceId, String metricId, String metricType,
                       Severity severity, AlarmStatus status, String description,
                       String alarmRegionCode, long createdTime) {
        this.alarmId = alarmId;
        this.deviceId = deviceId;
        this.metricId = metricId;
        this.metricType = metricType;
        this.severity = severity;
        this.status = status;
        this.description = description;
        this.alarmRegionCode = alarmRegionCode;
        this.createdTime = createdTime;
        this.updatedTime = createdTime;
    }

    public String getAlarmId() { return alarmId; }
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getMetricId() { return metricId; }
    public void setMetricId(String metricId) { this.metricId = metricId; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public AlarmStatus getStatus() { return status; }
    public void setStatus(AlarmStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAlarmRegionCode() { return alarmRegionCode; }
    public void setAlarmRegionCode(String alarmRegionCode) { this.alarmRegionCode = alarmRegionCode; }
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    public long getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(long updatedTime) { this.updatedTime = updatedTime; }
}
