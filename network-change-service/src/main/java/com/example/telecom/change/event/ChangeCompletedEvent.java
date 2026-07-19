package com.example.telecom.change.event;

/**
 * Spring event published when a change completes execution.
 */
public class ChangeCompletedEvent {
    private final String planId;
    private final boolean success;
    private final String message;

    public ChangeCompletedEvent(String planId, boolean success, String message) {
        this.planId = planId;
        this.success = success;
        this.message = message;
    }

    public String getPlanId() { return planId; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
