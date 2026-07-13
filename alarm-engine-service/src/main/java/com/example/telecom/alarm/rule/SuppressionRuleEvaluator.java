package com.example.telecom.alarm.rule;

import com.example.telecom.alarm.domain.AlarmSuppressionRule;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.Severity;

import java.util.Objects;

/**
 * Evaluates whether a specific alarm should be suppressed based on a
 * single suppression rule. Checks device pattern, alarm type pattern,
 * severity threshold, and time window constraints.
 */
public class SuppressionRuleEvaluator {

    /**
     * Evaluates the given alarm against the suppression rule.
     *
     * @param alarm the alarm to evaluate
     * @param rule  the suppression rule to apply
     * @return true if the alarm should be suppressed
     */
    public boolean evaluate(AlarmRecord alarm, AlarmSuppressionRule rule) {
        Objects.requireNonNull(alarm, "alarm must not be null");
        Objects.requireNonNull(rule, "rule must not be null");

        if (!rule.isEnabled()) {
            return false;
        }
        if (!matchDevice(alarm.getDeviceId(), rule.getDevicePattern())) {
            return false;
        }
        if (!matchAlarmType(alarm.getMetricType(), rule.getAlarmTypePattern())) {
            return false;
        }
        if (!severityBelowThreshold(alarm.getSeverity(), rule.getSeverityThreshold())) {
            return false;
        }
        return withinTimeWindow(alarm.getCreatedTime(), rule.getTimeWindowMinutes());
    }

    /**
     * Checks if the device ID matches the device pattern. Supports wildcard "*".
     */
    boolean matchDevice(String deviceId, String devicePattern) {
        if (devicePattern == null || devicePattern.isEmpty()) {
            return false;
        }
        if ("*".equals(devicePattern)) {
            return true;
        }
        return deviceId != null && deviceId.contains(devicePattern);
    }

    /**
     * Checks if the alarm type matches the alarm type pattern. Supports wildcard "*".
     */
    boolean matchAlarmType(String alarmType, String alarmTypePattern) {
        if (alarmTypePattern == null || alarmTypePattern.isEmpty()) {
            return false;
        }
        if ("*".equals(alarmTypePattern)) {
            return true;
        }
        return alarmType != null && alarmType.contains(alarmTypePattern);
    }

    /**
     * Checks if the alarm's severity is at or below the threshold severity.
     * Returns true if the alarm severity is less severe (lower ordinal) than
     * or equal to the threshold, meaning it should be suppressed.
     */
    private boolean severityBelowThreshold(Severity alarmSeverity, Severity threshold) {
        if (threshold == null) {
            return true;
        }
        if (alarmSeverity == null) {
            return false;
        }
        return alarmSeverity.ordinal() <= threshold.ordinal();
    }

    /**
     * Checks if the alarm was created within the suppression time window.
     */
    boolean withinTimeWindow(long alarmCreatedTime, long timeWindowMinutes) {
        if (timeWindowMinutes <= 0) {
            return true;
        }
        long now = System.currentTimeMillis();
        long windowMillis = timeWindowMinutes * 60 * 1000;
        return (now - alarmCreatedTime) <= windowMillis;
    }
}
