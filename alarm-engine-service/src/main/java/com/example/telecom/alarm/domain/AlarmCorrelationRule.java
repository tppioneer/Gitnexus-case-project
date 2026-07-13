package com.example.telecom.alarm.domain;

import com.example.telecom.common.alarm.Severity;

import java.util.Objects;

/**
 * Defines a rule for correlating alarms based on time windows, device matching,
 * alarm type matching, and severity ranges.
 */
public class AlarmCorrelationRule {

    private String ruleId;
    private String name;
    private String correlationType;
    private long timeWindowMinutes;
    private String deviceMatch;
    private String alarmTypeMatch;
    private Severity severityLow;
    private Severity severityHigh;
    private boolean enabled;

    public AlarmCorrelationRule() {
    }

    public AlarmCorrelationRule(String ruleId, String name, String correlationType,
                                 long timeWindowMinutes, String deviceMatch,
                                 String alarmTypeMatch, Severity severityLow,
                                 Severity severityHigh, boolean enabled) {
        this.ruleId = ruleId;
        this.name = name;
        this.correlationType = correlationType;
        this.timeWindowMinutes = timeWindowMinutes;
        this.deviceMatch = deviceMatch;
        this.alarmTypeMatch = alarmTypeMatch;
        this.severityLow = severityLow;
        this.severityHigh = severityHigh;
        this.enabled = enabled;
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

    public String getCorrelationType() {
        return correlationType;
    }

    public void setCorrelationType(String correlationType) {
        this.correlationType = correlationType;
    }

    public long getTimeWindowMinutes() {
        return timeWindowMinutes;
    }

    public void setTimeWindowMinutes(long timeWindowMinutes) {
        this.timeWindowMinutes = timeWindowMinutes;
    }

    public String getDeviceMatch() {
        return deviceMatch;
    }

    public void setDeviceMatch(String deviceMatch) {
        this.deviceMatch = deviceMatch;
    }

    public String getAlarmTypeMatch() {
        return alarmTypeMatch;
    }

    public void setAlarmTypeMatch(String alarmTypeMatch) {
        this.alarmTypeMatch = alarmTypeMatch;
    }

    public Severity getSeverityLow() {
        return severityLow;
    }

    public void setSeverityLow(Severity severityLow) {
        this.severityLow = severityLow;
    }

    public Severity getSeverityHigh() {
        return severityHigh;
    }

    public void setSeverityHigh(Severity severityHigh) {
        this.severityHigh = severityHigh;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmCorrelationRule that = (AlarmCorrelationRule) o;
        return Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }

    @Override
    public String toString() {
        return "AlarmCorrelationRule{ruleId='" + ruleId + "', name='" + name + "', type='" + correlationType + "'}";
    }
}
