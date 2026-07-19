package com.example.telecom.change.event;

/**
 * Spring event published when a change is approved.
 */
public class ChangeApprovedEvent {
    private final String planId;
    private final String approverId;
    private final boolean approved;

    public ChangeApprovedEvent(String planId, String approverId, boolean approved) {
        this.planId = planId;
        this.approverId = approverId;
        this.approved = approved;
    }

    public String getPlanId() { return planId; }
    public String getApproverId() { return approverId; }
    public boolean isApproved() { return approved; }
}
