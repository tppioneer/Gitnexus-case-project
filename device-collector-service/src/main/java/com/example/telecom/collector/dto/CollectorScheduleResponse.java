package com.example.telecom.collector.dto;

public class CollectorScheduleResponse {

    private String scheduleId;
    private String scheduleName;
    private String status;
    private int deviceCount;
    private int intervalSeconds;
    private long lastRunTime;
    private long nextRunTime;

    public CollectorScheduleResponse() {}

    public CollectorScheduleResponse(String scheduleId, String scheduleName, String status,
                                     int deviceCount, int intervalSeconds,
                                     long lastRunTime, long nextRunTime) {
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.status = status;
        this.deviceCount = deviceCount;
        this.intervalSeconds = intervalSeconds;
        this.lastRunTime = lastRunTime;
        this.nextRunTime = nextRunTime;
    }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public String getScheduleName() { return scheduleName; }
    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getDeviceCount() { return deviceCount; }
    public void setDeviceCount(int deviceCount) { this.deviceCount = deviceCount; }

    public int getIntervalSeconds() { return intervalSeconds; }
    public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }

    public long getLastRunTime() { return lastRunTime; }
    public void setLastRunTime(long lastRunTime) { this.lastRunTime = lastRunTime; }

    public long getNextRunTime() { return nextRunTime; }
    public void setNextRunTime(long nextRunTime) { this.nextRunTime = nextRunTime; }
}
