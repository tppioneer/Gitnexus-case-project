package com.example.telecom.workorder.dto;

import java.util.HashMap;
import java.util.Map;

public class WorkOrderReportResponse {

    private String reportId;
    private long generatedAt;
    private long startTime;
    private long endTime;
    private long totalWorkOrders;
    private String groupBy;
    private Map<String, Long> summaryData;
    private String format;

    public WorkOrderReportResponse() {
        this.summaryData = new HashMap<>();
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public long getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(long generatedAt) {
        this.generatedAt = generatedAt;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public Map<String, Long> getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(Map<String, Long> summaryData) {
        this.summaryData = summaryData;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
