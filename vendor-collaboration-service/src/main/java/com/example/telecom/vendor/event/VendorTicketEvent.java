package com.example.telecom.vendor.event;

import com.example.telecom.vendor.domain.VendorTicketStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class VendorTicketEvent {

    public enum EventType {
        CREATED,
        ACKNOWLEDGED,
        IN_PROGRESS,
        RESOLVED,
        ESCALATED,
        CLOSED
    }

    private final String eventId;
    private final String ticketId;
    private final String vendorId;
    private final EventType eventType;
    private final VendorTicketStatus previousStatus;
    private final VendorTicketStatus newStatus;
    private final LocalDateTime timestamp;
    private final String details;

    public VendorTicketEvent(String eventId, String ticketId, String vendorId,
                             EventType eventType, VendorTicketStatus previousStatus,
                             VendorTicketStatus newStatus, String details) {
        this.eventId = eventId;
        this.ticketId = ticketId;
        this.vendorId = vendorId;
        this.eventType = eventType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventId() { return eventId; }
    public String getTicketId() { return ticketId; }
    public String getVendorId() { return vendorId; }
    public EventType getEventType() { return eventType; }
    public VendorTicketStatus getPreviousStatus() { return previousStatus; }
    public VendorTicketStatus getNewStatus() { return newStatus; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDetails() { return details; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorTicketEvent that = (VendorTicketEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "VendorTicketEvent{" +
                "eventId='" + eventId + '\'' +
                ", ticketId='" + ticketId + '\'' +
                ", eventType=" + eventType +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", timestamp=" + timestamp +
                '}';
    }
}
