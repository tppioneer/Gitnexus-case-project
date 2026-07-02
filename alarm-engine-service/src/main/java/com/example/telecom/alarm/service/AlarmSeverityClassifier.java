package com.example.telecom.alarm.service;

import com.example.telecom.common.alarm.*;

/**
 * Classifies alarm severity based on evaluation result and threshold rule.
 */
public class AlarmSeverityClassifier {

    public Severity classify(EvaluationResult result, ThresholdRule rule) {
        if (!result.isTriggered()) {
            return Severity.INFO;
        }
        Severity ruleSeverity = rule.getSeverity();
        if (ruleSeverity == null) {
            ruleSeverity = Severity.WARNING;
        }
        // Escalate severity if value far exceeds threshold
        double ratio = result.getCurrentValue() / Math.max(result.getThresholdValue(), 0.01);
        if (ratio > 2.0 && ruleSeverity == Severity.WARNING) {
            return Severity.MAJOR;
        }
        if (ratio > 3.0 && ruleSeverity == Severity.MAJOR) {
            return Severity.CRITICAL;
        }
        return ruleSeverity;
    }
}
