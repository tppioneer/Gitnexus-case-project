package com.example.telecom.alarm.dto;

/**
 * Request DTO for alarm analytics queries.
 * Supports filtering by time range, aggregation type, severity, device, and date range.
 */
public class AlarmAnalyticsRequest {

    private String timeRange;
    private String aggregationType;
    private String severity;
    private String deviceId;
    private String fromDate;
    private String toDate;

    public AlarmAnalyticsRequest() {
    }

    public AlarmAnalyticsRequest(String timeRange, String aggregationType, String severity,
                                  String deviceId, String fromDate, String toDate) {
        this.timeRange = timeRange;
        this.aggregationType = aggregationType;
        this.severity = severity;
        this.deviceId = deviceId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
