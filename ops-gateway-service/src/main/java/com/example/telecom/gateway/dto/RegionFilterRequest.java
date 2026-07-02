package com.example.telecom.gateway.dto;

/**
 * Region filter request DTO.
 * Its regionCode is a DISTRACTION field for Case C — NOT to be modified.
 */
public class RegionFilterRequest {
    private String regionCode;

    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
}
