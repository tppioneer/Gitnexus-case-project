package com.example.telecom.common.event;

import com.example.telecom.common.maintenance.MaintenanceStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class MaintenancePlanEvent {

    private final String eventId;
    private final String planId;
    private final MaintenanceStatus previousStatus;
    private final MaintenanceStatus newStatus;
    private final String regionCode;
    private final LocalDateTime timestamp;

    public MaintenancePlanEvent(String eventId, String planId,
                                MaintenanceStatus previousStatus,
                                MaintenanceStatus newStatus,
                                String regionCode, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.planId = planId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.regionCode = regionCode;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public String getPlanId() { return planId; }
    public MaintenanceStatus getPreviousStatus() { return previousStatus; }
    public MaintenanceStatus getNewStatus() { return newStatus; }
    public String getRegionCode() { return regionCode; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenancePlanEvent that = (MaintenancePlanEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "MaintenancePlanEvent{" +
                "eventId='" + eventId + '\'' +
                ", planId='" + planId + '\'' +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", regionCode='" + regionCode + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
