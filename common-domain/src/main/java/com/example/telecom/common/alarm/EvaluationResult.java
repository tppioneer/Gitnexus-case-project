package com.example.telecom.common.alarm;

public class EvaluationResult {
    private boolean triggered;
    private String ruleId;
    private String metricId;
    private double currentValue;
    private double thresholdValue;
    private Severity severity;
    private String message;

    public EvaluationResult() {}

    public EvaluationResult(boolean triggered, String ruleId, String metricId, double currentValue,
                            double thresholdValue, Severity severity, String message) {
        this.triggered = triggered;
        this.ruleId = ruleId;
        this.metricId = metricId;
        this.currentValue = currentValue;
        this.thresholdValue = thresholdValue;
        this.severity = severity;
        this.message = message;
    }

    public boolean isTriggered() { return triggered; }
    public void setTriggered(boolean triggered) { this.triggered = triggered; }
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getMetricId() { return metricId; }
    public void setMetricId(String metricId) { this.metricId = metricId; }
    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }
    public double getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(double thresholdValue) { this.thresholdValue = thresholdValue; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
