package com.example.telecom.gateway.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class DashboardExportRequest {

    private String exportType;
    private String format;
    private String scope;
    private String scopeId;
    private Map<String, LocalDateTime> timeRange;
    private Boolean includeCharts;

    public DashboardExportRequest() {
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public Map<String, LocalDateTime> getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(Map<String, LocalDateTime> timeRange) {
        this.timeRange = timeRange;
    }

    public Boolean getIncludeCharts() {
        return includeCharts;
    }

    public void setIncludeCharts(Boolean includeCharts) {
        this.includeCharts = includeCharts;
    }
}
