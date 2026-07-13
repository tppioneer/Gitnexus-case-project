package com.example.telecom.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VendorTicketRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotBlank(message = "Vendor ID is required")
    private String vendorId;

    @NotBlank(message = "Vendor name is required")
    private String vendorName;

    private String deviceId;

    private String priority;

    private String regionCode;

    private String category;

    private String source;

    public VendorTicketRequest() {
    }

    public VendorTicketRequest(String title, String description, String vendorId,
                               String vendorName, String deviceId, String priority,
                               String regionCode) {
        this.title = title;
        this.description = description;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.deviceId = deviceId;
        this.priority = priority;
        this.regionCode = regionCode;
    }

    public @NotBlank @Size(max = 200) String getTitle() { return title; }
    public void setTitle(@NotBlank @Size(max = 200) String title) { this.title = title; }
    public @Size(max = 2000) String getDescription() { return description; }
    public void setDescription(@Size(max = 2000) String description) { this.description = description; }
    public @NotBlank String getVendorId() { return vendorId; }
    public void setVendorId(@NotBlank String vendorId) { this.vendorId = vendorId; }
    public @NotBlank String getVendorName() { return vendorName; }
    public void setVendorName(@NotBlank String vendorName) { this.vendorName = vendorName; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
