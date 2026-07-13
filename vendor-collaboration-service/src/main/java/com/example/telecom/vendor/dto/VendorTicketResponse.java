package com.example.telecom.vendor.dto;

import com.example.telecom.vendor.domain.VendorTicketStatus;
import java.time.LocalDateTime;

public class VendorTicketResponse {
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
    private String category;
    private String source;
    private long ageInHours;
    private int escalationCount;

    public VendorTicketResponse() {
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
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public long getAgeInHours() { return ageInHours; }
    public void setAgeInHours(long ageInHours) { this.ageInHours = ageInHours; }
    public int getEscalationCount() { return escalationCount; }
    public void setEscalationCount(int escalationCount) { this.escalationCount = escalationCount; }
}
