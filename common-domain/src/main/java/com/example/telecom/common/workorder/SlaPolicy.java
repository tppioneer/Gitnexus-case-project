package com.example.telecom.common.workorder;

import com.example.telecom.common.EscalationLevel;

public class SlaPolicy {
    private String slaPolicyId;
    private String priority;
    private long responseTimeMs;
    private long resolutionTimeMs;
    private EscalationLevel escalationLevel;
    private boolean active;

    public SlaPolicy() {}

    public SlaPolicy(String slaPolicyId, String priority, long responseTimeMs,
                     long resolutionTimeMs, EscalationLevel escalationLevel, boolean active) {
        this.slaPolicyId = slaPolicyId;
        this.priority = priority;
        this.responseTimeMs = responseTimeMs;
        this.resolutionTimeMs = resolutionTimeMs;
        this.escalationLevel = escalationLevel;
        this.active = active;
    }

    public boolean isBreachResponse(long elapsedMs) { return active && elapsedMs > responseTimeMs; }
    public boolean isBreachResolution(long elapsedMs) { return active && elapsedMs > resolutionTimeMs; }

    public String getSlaPolicyId() { return slaPolicyId; }
    public void setSlaPolicyId(String slaPolicyId) { this.slaPolicyId = slaPolicyId; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    public long getResolutionTimeMs() { return resolutionTimeMs; }
    public void setResolutionTimeMs(long resolutionTimeMs) { this.resolutionTimeMs = resolutionTimeMs; }
    public EscalationLevel getEscalationLevel() { return escalationLevel; }
    public void setEscalationLevel(EscalationLevel escalationLevel) { this.escalationLevel = escalationLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
