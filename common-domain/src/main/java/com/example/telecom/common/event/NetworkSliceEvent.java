package com.example.telecom.common.event;

import com.example.telecom.common.device.NetworkSliceStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class NetworkSliceEvent {

    private final String eventId;
    private final String sliceId;
    private final NetworkSliceStatus previousStatus;
    private final NetworkSliceStatus newStatus;
    private final String regionCode;
    private final LocalDateTime timestamp;

    public NetworkSliceEvent(String eventId, String sliceId,
                             NetworkSliceStatus previousStatus,
                             NetworkSliceStatus newStatus,
                             String regionCode, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.sliceId = sliceId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.regionCode = regionCode;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public String getSliceId() { return sliceId; }
    public NetworkSliceStatus getPreviousStatus() { return previousStatus; }
    public NetworkSliceStatus getNewStatus() { return newStatus; }
    public String getRegionCode() { return regionCode; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkSliceEvent that = (NetworkSliceEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "NetworkSliceEvent{" +
                "eventId='" + eventId + '\'' +
                ", sliceId='" + sliceId + '\'' +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", regionCode='" + regionCode + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
