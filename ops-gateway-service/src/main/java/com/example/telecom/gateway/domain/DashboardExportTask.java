package com.example.telecom.gateway.domain;

import java.time.LocalDateTime;
import java.util.Map;

public class DashboardExportTask {

    private String exportId;
    private String userId;
    private String exportType;
    private String format;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime completedTime;
    private Long fileSize;
    private Map<String, String> parameters;

    public DashboardExportTask() {
    }

    public DashboardExportTask(String exportId, String userId, String exportType,
                               String format, String status, LocalDateTime createdTime,
                               Map<String, String> parameters) {
        this.exportId = exportId;
        this.userId = userId;
        this.exportType = exportType;
        this.format = format;
        this.status = status;
        this.createdTime = createdTime;
        this.parameters = parameters;
    }

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
