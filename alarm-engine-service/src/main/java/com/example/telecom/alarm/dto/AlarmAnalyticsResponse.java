package com.example.telecom.alarm.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Response DTO for alarm analytics queries.
 * Contains the analytics type, data map, period, and generation timestamp.
 */
public class AlarmAnalyticsResponse {

    private String analyticsType;
    private Map<String, Object> data;
    private String period;
    private long generatedTime;

    public AlarmAnalyticsResponse() {
        this.data = new HashMap<>();
    }

    public AlarmAnalyticsResponse(String analyticsType, Map<String, Object> data,
                                   String period, long generatedTime) {
        this.analyticsType = analyticsType;
        this.data = data != null ? data : new HashMap<>();
        this.period = period;
        this.generatedTime = generatedTime;
    }

    public String getAnalyticsType() {
        return analyticsType;
    }

    public void setAnalyticsType(String analyticsType) {
        this.analyticsType = analyticsType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public long getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(long generatedTime) {
        this.generatedTime = generatedTime;
    }
}
