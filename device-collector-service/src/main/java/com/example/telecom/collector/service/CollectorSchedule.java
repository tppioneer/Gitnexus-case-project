package com.example.telecom.collector.service;

import java.util.List;
import java.util.Objects;

public class CollectorSchedule {

    public enum ScheduleStatus {
        CREATED,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED
    }

    private String scheduleId;
    private String scheduleName;
    private List<String> deviceIds;
    private List<String> metricTypes;
    private int intervalSeconds;
    private String cronExpression;
    private ScheduleStatus status;
    private long lastRunTime;
    private long nextRunTime;
    private long createdTime;

    public CollectorSchedule() {
        this.status = ScheduleStatus.CREATED;
        this.createdTime = System.currentTimeMillis();
    }

    public CollectorSchedule(String scheduleId, String scheduleName, List<String> deviceIds,
                             List<String> metricTypes, int intervalSeconds, String cronExpression) {
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.deviceIds = deviceIds;
        this.metricTypes = metricTypes;
        this.intervalSeconds = intervalSeconds;
        this.cronExpression = cronExpression;
        this.status = ScheduleStatus.CREATED;
        this.createdTime = System.currentTimeMillis();
    }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public String getScheduleName() { return scheduleName; }
    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }

    public List<String> getDeviceIds() { return deviceIds; }
    public void setDeviceIds(List<String> deviceIds) { this.deviceIds = deviceIds; }

    public List<String> getMetricTypes() { return metricTypes; }
    public void setMetricTypes(List<String> metricTypes) { this.metricTypes = metricTypes; }

    public int getIntervalSeconds() { return intervalSeconds; }
    public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public ScheduleStatus getStatus() { return status; }
    public void setStatus(ScheduleStatus status) { this.status = status; }

    public long getLastRunTime() { return lastRunTime; }
    public void setLastRunTime(long lastRunTime) { this.lastRunTime = lastRunTime; }

    public long getNextRunTime() { return nextRunTime; }
    public void setNextRunTime(long nextRunTime) { this.nextRunTime = nextRunTime; }

    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

    public int getDeviceCount() {
        return deviceIds == null ? 0 : deviceIds.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectorSchedule that)) return false;
        return Objects.equals(scheduleId, that.scheduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId);
    }

    @Override
    public String toString() {
        return "CollectorSchedule{" +
                "scheduleId='" + scheduleId + '\'' +
                ", scheduleName='" + scheduleName + '\'' +
                ", status=" + status +
                '}';
    }
}
