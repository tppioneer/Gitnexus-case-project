package com.example.telecom.gateway.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardHealthScoreResponse {

    private String scope;
    private String scopeId;
    private Double overallScore;
    private Double deviceScore;
    private Double alarmScore;
    private Double serviceScore;
    private LocalDateTime timestamp;
    private List<Map<String, Object>> history;

    public DashboardHealthScoreResponse() {
    }

    public DashboardHealthScoreResponse(String scope, String scopeId, Double overallScore,
                                        Double deviceScore, Double alarmScore,
                                        Double serviceScore, LocalDateTime timestamp,
                                        List<Map<String, Object>> history) {
        this.scope = scope;
        this.scopeId = scopeId;
        this.overallScore = overallScore;
        this.deviceScore = deviceScore;
        this.alarmScore = alarmScore;
        this.serviceScore = serviceScore;
        this.timestamp = timestamp;
        this.history = history;
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

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public Double getDeviceScore() {
        return deviceScore;
    }

    public void setDeviceScore(Double deviceScore) {
        this.deviceScore = deviceScore;
    }

    public Double getAlarmScore() {
        return alarmScore;
    }

    public void setAlarmScore(Double alarmScore) {
        this.alarmScore = alarmScore;
    }

    public Double getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(Double serviceScore) {
        this.serviceScore = serviceScore;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<Map<String, Object>> getHistory() {
        return history;
    }

    public void setHistory(List<Map<String, Object>> history) {
        this.history = history;
    }
}
