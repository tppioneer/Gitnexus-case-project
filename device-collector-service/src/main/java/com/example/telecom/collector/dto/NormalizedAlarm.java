package com.example.telecom.collector.dto;

import com.example.telecom.common.alarm.Severity;

public class NormalizedAlarm {

    private String sourceId;
    private String alarmType;
    private Severity severity;
    private String deviceId;
    private long timestamp;
    private String description;
    private String vendorType;
    private String rawData;

    public NormalizedAlarm() {}

    public NormalizedAlarm(String sourceId, String alarmType, Severity severity, String deviceId,
                           long timestamp, String description, String vendorType, String rawData) {
        this.sourceId = sourceId;
        this.alarmType = alarmType;
        this.severity = severity;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.description = description;
        this.vendorType = vendorType;
        this.rawData = rawData;
    }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getAlarmType() { return alarmType; }
    public void setAlarmType(String alarmType) { this.alarmType = alarmType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVendorType() { return vendorType; }
    public void setVendorType(String vendorType) { this.vendorType = vendorType; }

    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
}
