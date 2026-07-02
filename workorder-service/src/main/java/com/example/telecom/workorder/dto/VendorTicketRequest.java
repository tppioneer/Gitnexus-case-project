package com.example.telecom.workorder.dto;

public class VendorTicketRequest {
    private String workOrderId;
    private String deviceId;
    private String vendor;
    private String description;

    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
