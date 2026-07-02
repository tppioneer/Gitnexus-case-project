package com.example.telecom.common.alarm;

public class ThresholdRule {
    private String ruleId;
    private String ruleName;
    private String metricType;
    private double thresholdValue;
    private Severity severity;
    private boolean enabled;

    public ThresholdRule() {}

    public ThresholdRule(String ruleId, String ruleName, String metricType, double thresholdValue,
                         Severity severity, boolean enabled) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.metricType = metricType;
        this.thresholdValue = thresholdValue;
        this.severity = severity;
        this.enabled = enabled;
    }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public double getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(double thresholdValue) { this.thresholdValue = thresholdValue; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
