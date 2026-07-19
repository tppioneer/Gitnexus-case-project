package com.example.telecom.change.domain;

/**
 * Base command for the change command bus. Subclasses may add fields
 * specific to particular change types (e.g. emergency metadata).
 */
public class ChangeCommand {
    private final String changeId;
    private final ChangeMode mode;
    private final String description;

    public ChangeCommand(String changeId, ChangeMode mode, String description) {
        this.changeId = changeId;
        this.mode = mode;
        this.description = description;
    }

    public String getChangeId() { return changeId; }
    public ChangeMode getMode() { return mode; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "ChangeCommand{id='" + changeId + "', mode=" + mode + "}";
    }
}
