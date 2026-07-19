package com.example.telecom.change.dto;

import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.DeviceFamily;

/**
 * Request DTO for creating or updating a network change.
 */
public class ChangeRequest {
    private String title;
    private String regionCode;
    private DeviceFamily deviceFamily;
    private ChangeRisk risk;
    private ChangeMode mode;
    private String description;

    public ChangeRequest() {}

    public ChangeRequest(String title, String regionCode, DeviceFamily deviceFamily,
                         ChangeRisk risk, ChangeMode mode, String description) {
        this.title = title;
        this.regionCode = regionCode;
        this.deviceFamily = deviceFamily;
        this.risk = risk;
        this.mode = mode;
        this.description = description;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public DeviceFamily getDeviceFamily() { return deviceFamily; }
    public void setDeviceFamily(DeviceFamily deviceFamily) { this.deviceFamily = deviceFamily; }
    public ChangeRisk getRisk() { return risk; }
    public void setRisk(ChangeRisk risk) { this.risk = risk; }
    public ChangeMode getMode() { return mode; }
    public void setMode(ChangeMode mode) { this.mode = mode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
