package com.example.telecom.gateway.dto;

import java.util.Map;

public class DashboardHealthScoreRequest {

    private String scope;
    private String scopeId;
    private Map<String, String> timeRange;
    private Boolean includeHistory;

    public DashboardHealthScoreRequest() {
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

    public Map<String, String> getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(Map<String, String> timeRange) {
        this.timeRange = timeRange;
    }

    public Boolean getIncludeHistory() {
        return includeHistory;
    }

    public void setIncludeHistory(Boolean includeHistory) {
        this.includeHistory = includeHistory;
    }
}
