package com.example.telecom.common.workorder;

public class SlaBreachEvent {
    private String eventId;
    private String workOrderId;
    private String slaPolicyId;
    private String breachType; // RESPONSE, RESOLUTION
    private long elapsedMs;
    private long thresholdMs;
    private long eventTimestamp;

    public SlaBreachEvent() {}

    public SlaBreachEvent(String eventId, String workOrderId, String slaPolicyId,
                           String breachType, long elapsedMs, long thresholdMs) {
        this.eventId = eventId;
        this.workOrderId = workOrderId;
        this.slaPolicyId = slaPolicyId;
        this.breachType = breachType;
        this.elapsedMs = elapsedMs;
        this.thresholdMs = thresholdMs;
        this.eventTimestamp = System.currentTimeMillis();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getSlaPolicyId() { return slaPolicyId; }
    public void setSlaPolicyId(String slaPolicyId) { this.slaPolicyId = slaPolicyId; }
    public String getBreachType() { return breachType; }
    public void setBreachType(String breachType) { this.breachType = breachType; }
    public long getElapsedMs() { return elapsedMs; }
    public void setElapsedMs(long elapsedMs) { this.elapsedMs = elapsedMs; }
    public long getThresholdMs() { return thresholdMs; }
    public void setThresholdMs(long thresholdMs) { this.thresholdMs = thresholdMs; }
    public long getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(long eventTimestamp) { this.eventTimestamp = eventTimestamp; }
}
