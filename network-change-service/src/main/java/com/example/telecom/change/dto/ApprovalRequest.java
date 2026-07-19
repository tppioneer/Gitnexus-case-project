package com.example.telecom.change.dto;

/**
 * Request DTO for approving a change.
 */
public class ApprovalRequest {
    private String planId;
    private String approverId;
    private boolean approved;
    private String comment;

    public ApprovalRequest() {}

    public ApprovalRequest(String planId, String approverId, boolean approved, String comment) {
        this.planId = planId;
        this.approverId = approverId;
        this.approved = approved;
        this.comment = comment;
    }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    public String getApproverId() { return approverId; }
    public void setApproverId(String approverId) { this.approverId = approverId; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
