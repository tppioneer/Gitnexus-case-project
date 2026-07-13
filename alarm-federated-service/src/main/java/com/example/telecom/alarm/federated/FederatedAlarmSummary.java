package com.example.telecom.alarm.federated;

import java.time.LocalDateTime;
import java.util.Map;

public class FederatedAlarmSummary {

    private long totalCount;
    private Map<String, Long> bySeverity;
    private Map<String, Long> byRegion;
    private String timeRange;
    private LocalDateTime lastUpdated;

    public FederatedAlarmSummary() {
    }

    public FederatedAlarmSummary(long totalCount, Map<String, Long> bySeverity, Map<String, Long> byRegion,
                                  String timeRange, LocalDateTime lastUpdated) {
        this.totalCount = totalCount;
        this.bySeverity = bySeverity;
        this.byRegion = byRegion;
        this.timeRange = timeRange;
        this.lastUpdated = lastUpdated;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Map<String, Long> getBySeverity() {
        return bySeverity;
    }

    public void setBySeverity(Map<String, Long> bySeverity) {
        this.bySeverity = bySeverity;
    }

    public Map<String, Long> getByRegion() {
        return byRegion;
    }

    public void setByRegion(Map<String, Long> byRegion) {
        this.byRegion = byRegion;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
