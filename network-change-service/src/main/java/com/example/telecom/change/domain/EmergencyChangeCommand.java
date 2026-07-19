package com.example.telecom.change.domain;

/**
 * Emergency change command — a subclass of {@code ChangeCommand} used to
 * test overload resolution. Carries an additional severity level.
 */
public class EmergencyChangeCommand extends ChangeCommand {
    private final ChangeRisk severity;
    private final String escalationContact;

    public EmergencyChangeCommand(String changeId, ChangeMode mode, String description,
                                  ChangeRisk severity, String escalationContact) {
        super(changeId, mode, description);
        this.severity = severity;
        this.escalationContact = escalationContact;
    }

    public ChangeRisk getSeverity() { return severity; }
    public String getEscalationContact() { return escalationContact; }
}
