package com.example.telecom.gateway.dto;

import java.time.LocalDateTime;

public class DashboardExportResponse {

    private String exportId;
    private String exportType;
    private String format;
    private String status;
    private Integer progress;
    private LocalDateTime createdTime;
    private String downloadUrl;

    public DashboardExportResponse() {
    }

    public DashboardExportResponse(String exportId, String exportType, String format,
                                   String status, Integer progress,
                                   LocalDateTime createdTime, String downloadUrl) {
        this.exportId = exportId;
        this.exportType = exportType;
        this.format = format;
        this.status = status;
        this.progress = progress;
        this.createdTime = createdTime;
        this.downloadUrl = downloadUrl;
    }

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
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

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
