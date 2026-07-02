package com.example.telecom.alarm.dto;

public class AlarmExportRequest {
    private String format; // CSV, JSON
    private String regionCode;
    private String severity;
    private long startTime;
    private long endTime;

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
}
