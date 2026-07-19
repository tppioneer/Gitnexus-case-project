package com.example.telecom.change.domain;

/**
 * Result of dispatching a command through {@code ChangeCommandBus}.
 */
public final class DispatchResult {
    private final String dispatchedTo;
    private final boolean accepted;
    private final String details;

    public DispatchResult(String dispatchedTo, boolean accepted, String details) {
        this.dispatchedTo = dispatchedTo;
        this.accepted = accepted;
        this.details = details;
    }

    public String getDispatchedTo() { return dispatchedTo; }
    public boolean isAccepted() { return accepted; }
    public String getDetails() { return details; }

    public static DispatchResult accepted(String target, String details) {
        return new DispatchResult(target, true, details);
    }

    public static DispatchResult rejected(String target, String details) {
        return new DispatchResult(target, false, details);
    }
}
