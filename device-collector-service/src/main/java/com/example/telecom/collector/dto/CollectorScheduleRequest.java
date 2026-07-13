package com.example.telecom.collector.dto;

import java.util.List;

public class CollectorScheduleRequest {

    private String scheduleName;
    private List<String> deviceIds;
    private List<String> metricTypes;
    private int intervalSeconds;
    private String cronExpression;
    private boolean enabled;

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

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
