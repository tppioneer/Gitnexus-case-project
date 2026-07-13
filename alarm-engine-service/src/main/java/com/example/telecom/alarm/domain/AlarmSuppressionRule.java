package com.example.telecom.alarm.domain;

import com.example.telecom.common.alarm.Severity;

import java.util.Objects;

/**
 * Represents a rule that determines whether an alarm should be suppressed
 * based on device pattern, alarm type, severity threshold, and time window.
 */
public class AlarmSuppressionRule {

    private String ruleId;
    private String name;
    private String devicePattern;
    private String alarmTypePattern;
    private Severity severityThreshold;
    private long timeWindowMinutes;
    private boolean enabled;
    private long createdTime;

    public AlarmSuppressionRule() {
    }

    public AlarmSuppressionRule(String ruleId, String name, String devicePattern,
                                 String alarmTypePattern, Severity severityThreshold,
                                 long timeWindowMinutes, boolean enabled, long createdTime) {
        this.ruleId = ruleId;
        this.name = name;
        this.devicePattern = devicePattern;
        this.alarmTypePattern = alarmTypePattern;
        this.severityThreshold = severityThreshold;
        this.timeWindowMinutes = timeWindowMinutes;
        this.enabled = enabled;
        this.createdTime = createdTime;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevicePattern() {
        return devicePattern;
    }

    public void setDevicePattern(String devicePattern) {
        this.devicePattern = devicePattern;
    }

    public String getAlarmTypePattern() {
        return alarmTypePattern;
    }

    public void setAlarmTypePattern(String alarmTypePattern) {
        this.alarmTypePattern = alarmTypePattern;
    }

    public Severity getSeverityThreshold() {
        return severityThreshold;
    }

    public void setSeverityThreshold(Severity severityThreshold) {
        this.severityThreshold = severityThreshold;
    }

    public long getTimeWindowMinutes() {
        return timeWindowMinutes;
    }

    public void setTimeWindowMinutes(long timeWindowMinutes) {
        this.timeWindowMinutes = timeWindowMinutes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmSuppressionRule that = (AlarmSuppressionRule) o;
        return Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }

    @Override
    public String toString() {
        return "AlarmSuppressionRule{ruleId='" + ruleId + "', name='" + name + "'}";
    }
}
