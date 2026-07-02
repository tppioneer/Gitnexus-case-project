package com.example.telecom.alarm.dto;

public class AlarmExportResponse {
    private String jobId;
    private String status;
    private int alarmCount;
    private String format;

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getAlarmCount() { return alarmCount; }
    public void setAlarmCount(int alarmCount) { this.alarmCount = alarmCount; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
