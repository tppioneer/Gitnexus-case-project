package com.example.telecom.change.dto;

import com.example.telecom.change.domain.ChangeStatus;

import java.time.LocalDateTime;

/**
 * Response DTO returned from change queries.
 */
public class ChangeResponse {
    private String planId;
    private String title;
    private ChangeStatus status;
    private String regionCode;
    private LocalDateTime createdAt;

    public ChangeResponse() {}

    public ChangeResponse(String planId, String title, ChangeStatus status,
                          String regionCode, LocalDateTime createdAt) {
        this.planId = planId;
        this.title = title;
        this.status = status;
        this.regionCode = regionCode;
        this.createdAt = createdAt;
    }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public ChangeStatus getStatus() { return status; }
    public void setStatus(ChangeStatus status) { this.status = status; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
