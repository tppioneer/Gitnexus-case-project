package com.example.telecom.common.region;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Region entity with its own regionCode.
 * This is a DISTRACTION field for Case C — NOT to be modified when DeviceInfo.regionCode is renamed.
 */
public class Region {
    private String regionId;
    private String regionName;

    @JsonProperty("regionCode")
    private String regionCode;

    private String parentRegionId;

    public Region() {}

    public Region(String regionId, String regionName, String regionCode, String parentRegionId) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.regionCode = regionCode;
        this.parentRegionId = parentRegionId;
    }

    public String getRegionId() { return regionId; }
    public void setRegionId(String regionId) { this.regionId = regionId; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getParentRegionId() { return parentRegionId; }
    public void setParentRegionId(String parentRegionId) { this.parentRegionId = parentRegionId; }
}
