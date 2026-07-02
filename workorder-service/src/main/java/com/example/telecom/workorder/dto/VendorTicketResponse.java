package com.example.telecom.workorder.dto;

public class VendorTicketResponse {
    private String vendorTicketId;
    private String workOrderId;
    private String vendor;
    private String status;
    private String vendorReference;
    private long createdTime;

    public String getVendorTicketId() { return vendorTicketId; }
    public void setVendorTicketId(String vendorTicketId) { this.vendorTicketId = vendorTicketId; }
    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVendorReference() { return vendorReference; }
    public void setVendorReference(String vendorReference) { this.vendorReference = vendorReference; }
    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }
}
