package com.example.telecom.alarm.dto;

public class ThresholdRuleRequest {
    private String ruleName;
    private String metricType;
    private double thresholdValue;
    private String severity;
    private boolean enabled;

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public double getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(double thresholdValue) { this.thresholdValue = thresholdValue; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
