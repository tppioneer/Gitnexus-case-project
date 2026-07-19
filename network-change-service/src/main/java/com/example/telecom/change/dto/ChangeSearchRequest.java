package com.example.telecom.change.dto;

import com.example.telecom.change.domain.ChangeStatus;

/**
 * Request DTO for searching/filtering changes.
 */
public class ChangeSearchRequest {
    private String regionCode;
    private ChangeStatus status;
    private String deviceFamily;
    private int page;
    private int size;

    public ChangeSearchRequest() {}

    public ChangeSearchRequest(String regionCode, ChangeStatus status, String deviceFamily,
                               int page, int size) {
        this.regionCode = regionCode;
        this.status = status;
        this.deviceFamily = deviceFamily;
        this.page = page;
        this.size = size;
    }

    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public ChangeStatus getStatus() { return status; }
    public void setStatus(ChangeStatus status) { this.status = status; }
    public String getDeviceFamily() { return deviceFamily; }
    public void setDeviceFamily(String deviceFamily) { this.deviceFamily = deviceFamily; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
