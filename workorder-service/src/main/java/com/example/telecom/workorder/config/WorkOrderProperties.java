package com.example.telecom.workorder.config;

public class WorkOrderProperties {
    private boolean autoCreateEnabled = true;
    private String defaultAssignmentStrategy = "RegionBased";
    private int escalationCheckIntervalMs = 60000;

    public boolean isAutoCreateEnabled() { return autoCreateEnabled; }
    public void setAutoCreateEnabled(boolean autoCreateEnabled) { this.autoCreateEnabled = autoCreateEnabled; }
    public String getDefaultAssignmentStrategy() { return defaultAssignmentStrategy; }
    public void setDefaultAssignmentStrategy(String defaultAssignmentStrategy) { this.defaultAssignmentStrategy = defaultAssignmentStrategy; }
    public int getEscalationCheckIntervalMs() { return escalationCheckIntervalMs; }
    public void setEscalationCheckIntervalMs(int escalationCheckIntervalMs) { this.escalationCheckIntervalMs = escalationCheckIntervalMs; }
}
