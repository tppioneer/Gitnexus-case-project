package com.example.telecom.common.workorder;

public class VendorTicket {
    private String vendorTicketId;
    private String workOrderId;
    private String deviceId;
    private String vendor;
    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    private String description;
    private String vendorReference;
    private long createdTime;
    private long updatedTime;

    public VendorTicket() {}

    public VendorTicket(String vendorTicketId, String workOrderId, String deviceId,
                         String vendor, String status, String description) {
        this.vendorTicketId = vendorTicketId;
        this.workOrderId = workOrderId;
        this.deviceId = deviceId;
        this.vendor = vendor;
        this.status = status;
        this.description = description;
        this.createdTime = System.currentTimeMillis();
        this.updatedTime = this.createdTime;
    }

    public String getVendorTicketId() { return vendorTicketId; }
    public void setVendorTicketId(String vendorTicketId) { this.vendorTicketId = vendorTicketId; }
    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVendorReference() { return vendorReference; }
    public void setVendorReference(String vendorReference) { this.vendorReference = vendorReference; }
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
    public long getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(long updatedTime) { this.updatedTime = updatedTime; }
}
