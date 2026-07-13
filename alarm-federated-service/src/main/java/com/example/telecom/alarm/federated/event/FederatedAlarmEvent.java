package com.example.telecom.alarm.federated.event;

import com.example.telecom.common.alarm.Severity;

import java.time.LocalDateTime;

public class FederatedAlarmEvent {

    private final String alarmId;
    private final String eventType;
    private final String sourceId;
    private final Severity severity;
    private final String regionOrGroup;
    private final LocalDateTime timestamp;

    public FederatedAlarmEvent(String alarmId, String eventType, String sourceId,
                                Severity severity, String regionOrGroup, LocalDateTime timestamp) {
        this.alarmId = alarmId;
        this.eventType = eventType;
        this.sourceId = sourceId;
        this.severity = severity;
        this.regionOrGroup = regionOrGroup;
        this.timestamp = timestamp;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getRegionOrGroup() {
        return regionOrGroup;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
