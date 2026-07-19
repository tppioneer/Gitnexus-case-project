package com.example.telecom.change.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain representation of a network change plan. Holds the plan identifier,
 * scope, risk, status, and arbitrary metadata accumulated during planning.
 */
public class NetworkChangePlan {
    private String planId;
    private String title;
    private String regionCode;
    private DeviceFamily deviceFamily;
    private ChangeRisk risk;
    private ChangeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, String> metadata;

    public NetworkChangePlan() {
        this.metadata = new HashMap<>();
        this.status = ChangeStatus.DRAFT;
    }

    public NetworkChangePlan(String planId, String title, String regionCode,
                             DeviceFamily deviceFamily, ChangeRisk risk) {
        this();
        this.planId = planId;
        this.title = title;
        this.regionCode = regionCode;
        this.deviceFamily = deviceFamily;
        this.risk = risk;
        this.createdAt = LocalDateTime.now();
    }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public DeviceFamily getDeviceFamily() { return deviceFamily; }
    public void setDeviceFamily(DeviceFamily deviceFamily) { this.deviceFamily = deviceFamily; }
    public ChangeRisk getRisk() { return risk; }
    public void setRisk(ChangeRisk risk) { this.risk = risk; }
    public ChangeStatus getStatus() { return status; }
    public void setStatus(ChangeStatus status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Map<String, String> getMetadata() { return Collections.unmodifiableMap(metadata); }

    public void putMetadata(String key, String value) {
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    public void markRunning() {
        this.status = ChangeStatus.RUNNING;
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompleted() {
        this.status = ChangeStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "NetworkChangePlan{planId='" + planId + "', status=" + status + ", risk=" + risk + "}";
    }
}
