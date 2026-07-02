package com.example.telecom.common.gateway;

public class ExportJob {
    private String jobId;
    private String type; // DASHBOARD, ALARM, WORKORDER, DEVICE
    private String format; // CSV, JSON, PDF
    private String status; // PENDING, RUNNING, COMPLETED, FAILED
    private String regionCode;
    private long createdTime;
    private long completedTime;
    private String filePath;

    public ExportJob() {}

    public ExportJob(String jobId, String type, String format, String status, String regionCode) {
        this.jobId = jobId;
        this.type = type;
        this.format = format;
        this.status = status;
        this.regionCode = regionCode;
        this.createdTime = System.currentTimeMillis();
    }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    public long getCompletedTime() { return completedTime; }
    public void setCompletedTime(long completedTime) { this.completedTime = completedTime; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
