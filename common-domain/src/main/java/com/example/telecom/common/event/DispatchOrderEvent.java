package com.example.telecom.common.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class DispatchOrderEvent {

    private final String eventId;
    private final String orderId;
    private final String previousStatus;
    private final String newStatus;
    private final LocalDateTime timestamp;
    private final String regionCode;

    public DispatchOrderEvent(String eventId, String orderId, String previousStatus,
                              String newStatus, LocalDateTime timestamp,
                              String regionCode) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
        this.regionCode = regionCode;
    }

    public String getEventId() { return eventId; }
    public String getOrderId() { return orderId; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRegionCode() { return regionCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispatchOrderEvent that = (DispatchOrderEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "DispatchOrderEvent{" +
                "eventId='" + eventId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", previousStatus='" + previousStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", timestamp=" + timestamp +
                ", regionCode='" + regionCode + '\'' +
                '}';
    }
}
