package com.example.telecom.alarm.federated.aggregation;

import java.time.LocalDateTime;
import java.util.Map;

public class AggregationResult {

    private String strategy;
    private Map<String, Object> groupedData;
    private long totalCount;
    private String timeRange;

    public AggregationResult() {
    }

    public AggregationResult(String strategy, Map<String, Object> groupedData, long totalCount, String timeRange) {
        this.strategy = strategy;
        this.groupedData = groupedData;
        this.totalCount = totalCount;
        this.timeRange = timeRange;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public Map<String, Object> getGroupedData() {
        return groupedData;
    }

    public void setGroupedData(Map<String, Object> groupedData) {
        this.groupedData = groupedData;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}
