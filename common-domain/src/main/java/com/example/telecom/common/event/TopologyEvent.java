package com.example.telecom.common.event;

import com.example.telecom.common.topology.TopologyChangeType;
import java.time.LocalDateTime;
import java.util.Objects;

public class TopologyEvent {

    private final String eventId;
    private final String nodeId;
    private final TopologyChangeType changeType;
    private final String previousStatus;
    private final String newStatus;
    private final String regionCode;
    private final LocalDateTime timestamp;

    public TopologyEvent(String eventId, String nodeId, TopologyChangeType changeType,
                         String previousStatus, String newStatus,
                         String regionCode, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.nodeId = nodeId;
        this.changeType = changeType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.regionCode = regionCode;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public String getNodeId() { return nodeId; }
    public TopologyChangeType getChangeType() { return changeType; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public String getRegionCode() { return regionCode; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopologyEvent that = (TopologyEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "TopologyEvent{" +
                "eventId='" + eventId + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", changeType=" + changeType +
                ", previousStatus='" + previousStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
