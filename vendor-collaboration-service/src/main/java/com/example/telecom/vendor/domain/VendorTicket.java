package com.example.telecom.vendor.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class VendorTicket {
    private String ticketId;
    private String title;
    private String description;
    private String vendorId;
    private String vendorName;
    private String deviceId;
    private VendorTicketStatus status;
    private String priority;
    private LocalDateTime createdTime;
    private LocalDateTime acknowledgedTime;
    private LocalDateTime inProgressTime;
    private LocalDateTime resolvedTime;
    private LocalDateTime closedTime;
    private String regionCode;
    private int escalationCount;

    public VendorTicket() {
        this.createdTime = LocalDateTime.now();
        this.escalationCount = 0;
    }

    public VendorTicket(String ticketId, String title, String description, String vendorId,
                        String vendorName, String deviceId, VendorTicketStatus status,
                        String priority, String regionCode) {
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.deviceId = deviceId;
        this.status = status;
        this.priority = priority;
        this.regionCode = regionCode;
        this.createdTime = LocalDateTime.now();
        this.escalationCount = 0;
    }

    public long getAgeInHours() {
        return Duration.between(createdTime, LocalDateTime.now()).toHours();
    }

    public long getResponseTimeInHours() {
        if (acknowledgedTime == null) {
            return 0;
        }
        return Duration.between(createdTime, acknowledgedTime).toHours();
    }

    public long getResolutionTimeInHours() {
        if (resolvedTime == null) {
            return 0;
        }
        return Duration.between(createdTime, resolvedTime).toHours();
    }

    public boolean isOverdue() {
        return "HIGH".equalsIgnoreCase(priority) && getAgeInHours() > 24;
    }

    public void incrementEscalationCount() {
        this.escalationCount++;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public VendorTicketStatus getStatus() { return status; }
    public void setStatus(VendorTicketStatus status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getAcknowledgedTime() { return acknowledgedTime; }
    public void setAcknowledgedTime(LocalDateTime acknowledgedTime) { this.acknowledgedTime = acknowledgedTime; }
    public LocalDateTime getInProgressTime() { return inProgressTime; }
    public void setInProgressTime(LocalDateTime inProgressTime) { this.inProgressTime = inProgressTime; }
    public LocalDateTime getResolvedTime() { return resolvedTime; }
    public void setResolvedTime(LocalDateTime resolvedTime) { this.resolvedTime = resolvedTime; }
    public LocalDateTime getClosedTime() { return closedTime; }
    public void setClosedTime(LocalDateTime closedTime) { this.closedTime = closedTime; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public int getEscalationCount() { return escalationCount; }
    public void setEscalationCount(int escalationCount) { this.escalationCount = escalationCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorTicket that = (VendorTicket) o;
        return Objects.equals(ticketId, that.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        return "VendorTicket{" +
                "ticketId='" + ticketId + '\'' +
                ", title='" + title + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", status=" + status +
                ", priority='" + priority + '\'' +
                '}';
    }
}
