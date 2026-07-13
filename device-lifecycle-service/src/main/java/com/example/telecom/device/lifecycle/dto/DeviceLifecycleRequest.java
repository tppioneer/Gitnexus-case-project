package com.example.telecom.device.lifecycle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DeviceLifecycleRequest {

    @NotBlank(message = "Device ID is required")
    @Pattern(regexp = "^DEV-[A-Z0-9]{6,12}$", message = "Device ID must match pattern DEV-XXXXXXXX")
    private String deviceId;

    @NotBlank(message = "Device name is required")
    private String deviceName;

    @NotBlank(message = "Device type is required")
    private String deviceType;

    @NotBlank(message = "Vendor is required")
    private String vendor;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Region code is required")
    private String regionCode;

    private String ipAddress;

    public DeviceLifecycleRequest() {}

    public DeviceLifecycleRequest(String deviceId, String deviceName, String deviceType,
                                   String vendor, String model, String regionCode, String ipAddress) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.vendor = vendor;
        this.model = model;
        this.regionCode = regionCode;
        this.ipAddress = ipAddress;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
