package com.example.telecom.change.dto;

import com.example.telecom.change.domain.ChangeRisk;

/**
 * Request DTO for updating the risk level of a change.
 */
public class RiskUpdateRequest {
    private String planId;
    private ChangeRisk newRisk;
    private String reason;

    public RiskUpdateRequest() {}

    public RiskUpdateRequest(String planId, ChangeRisk newRisk, String reason) {
        this.planId = planId;
        this.newRisk = newRisk;
        this.reason = reason;
    }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public ChangeRisk getNewRisk() { return newRisk; }
    public void setNewRisk(ChangeRisk newRisk) { this.newRisk = newRisk; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
