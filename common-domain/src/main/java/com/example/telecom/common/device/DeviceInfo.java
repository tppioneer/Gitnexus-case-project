package com.example.telecom.common.device;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceInfo {
    private String deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private String vendor;

    /** Case C source field: device maintenance region code. Renamed from regionCode to maintenanceRegionCode. */
    @JsonProperty("maintenanceRegionCode")
    private String maintenanceRegionCode;

    private String siteCode;
    private String managementIp;
    private boolean active;

    public DeviceInfo() {}

    public DeviceInfo(String deviceId, String deviceName, DeviceType deviceType, String vendor,
                      String maintenanceRegionCode, String siteCode, String managementIp, boolean active) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.vendor = vendor;
        this.maintenanceRegionCode = maintenanceRegionCode;
        this.siteCode = siteCode;
        this.managementIp = managementIp;
        this.active = active;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getMaintenanceRegionCode() { return maintenanceRegionCode; }
    public void setMaintenanceRegionCode(String maintenanceRegionCode) { this.maintenanceRegionCode = maintenanceRegionCode; }
    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }
    public String getManagementIp() { return managementIp; }
    public void setManagementIp(String managementIp) { this.managementIp = managementIp; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
