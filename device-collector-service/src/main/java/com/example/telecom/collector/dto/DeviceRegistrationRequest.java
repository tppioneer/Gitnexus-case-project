package com.example.telecom.collector.dto;

import com.example.telecom.common.device.DeviceType;

public class DeviceRegistrationRequest {
    private String deviceName;
    private DeviceType deviceType;
    private String vendor;
    private String regionCode;
    private String siteCode;
    private String managementIp;

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public String getManagementIp() { return managementIp; }
    public void setManagementIp(String managementIp) { this.managementIp = managementIp; }
}
