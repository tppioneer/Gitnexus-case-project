package com.example.telecom.alarm.service;

import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.Severity;

import java.time.Duration;
import java.time.Instant;

/**
 * Evaluates whether an alarm should be escalated based on age and severity.
 * Has an evaluate() method — another Case B noise item in the alarm-engine module.
 * This is NOT a RuleEvaluator, so it should NOT be modified in Case B.
 */
public class AlarmEscalationEvaluator {

    private final long criticalTimeoutMinutes;
    private final long majorTimeoutMinutes;
    private final long warningTimeoutMinutes;

    public AlarmEscalationEvaluator(long criticalTimeoutMinutes, long majorTimeoutMinutes,
                                     long warningTimeoutMinutes) {
        this.criticalTimeoutMinutes = criticalTimeoutMinutes;
        this.majorTimeoutMinutes = majorTimeoutMinutes;
        this.warningTimeoutMinutes = warningTimeoutMinutes;
    }

    public boolean evaluate(AlarmRecord alarm) {
        long ageMinutes = Duration.ofMillis(
                System.currentTimeMillis() - alarm.getCreatedTime()).toMinutes();

        return switch (alarm.getSeverity()) {
            case CRITICAL -> ageMinutes >= criticalTimeoutMinutes;
            case MAJOR -> ageMinutes >= majorTimeoutMinutes;
            case WARNING -> ageMinutes >= warningTimeoutMinutes;
            case INFO -> false;
        };
    }

    public String evaluate(AlarmRecord alarm, Instant referenceTime) {
        long ageMinutes = Duration.between(
                Instant.ofEpochMilli(alarm.getCreatedTime()), referenceTime).toMinutes();
        long threshold = getThreshold(alarm.getSeverity());
        if (ageMinutes >= threshold) {
            return "ESCALATE: Alarm " + alarm.getAlarmId() + " aged " + ageMinutes
                    + " min, threshold " + threshold + " min";
        }
        long remaining = threshold - ageMinutes;
        return "OK: " + remaining + " minutes until escalation for alarm " + alarm.getAlarmId();
    }

    private long getThreshold(Severity severity) {
        return switch (severity) {
            case CRITICAL -> criticalTimeoutMinutes;
            case MAJOR -> majorTimeoutMinutes;
            case WARNING -> warningTimeoutMinutes;
            case INFO -> Long.MAX_VALUE;
        };
    }
}
