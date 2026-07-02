package com.example.telecom.alarm.policy;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.Severity;

public class SeverityAlarmEscalationPolicy implements AlarmEscalationPolicy {

    @Override
    public boolean shouldEscalate(AlarmRecord alarm) {
        return alarm.getSeverity() == Severity.CRITICAL || alarm.getSeverity() == Severity.MAJOR;
    }

    @Override
    public EscalationLevel getLevel() {
        return EscalationLevel.LEVEL_2;
    }

    @Override
    public String evaluate(AlarmRecord alarm) {
        return shouldEscalate(alarm)
                ? "ESCALATE: Severity " + alarm.getSeverity() + " for alarm " + alarm.getAlarmId()
                : "OK: Severity " + alarm.getSeverity() + " within acceptable range";
    }
}
