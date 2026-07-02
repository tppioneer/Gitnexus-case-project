package com.example.telecom.alarm.service;

import com.example.telecom.common.alarm.AlarmRecord;

/**
 * Risk evaluation service. Has an evaluate() method but does NOT implement RuleEvaluator.
 * This is a Case B NOISE item — grep will find this evaluate(), but it should not be modified.
 */
public class RiskEvaluator {

    public int evaluate(AlarmRecord alarm) {
        // Simple risk scoring based on severity
        return switch (alarm.getSeverity()) {
            case CRITICAL -> 100;
            case MAJOR -> 70;
            case WARNING -> 40;
            case INFO -> 10;
        };
    }

    public String evaluate(String regionCode, int alarmCount) {
        return "Regional risk for " + regionCode + ": " + Math.min(100, alarmCount * 10) + "%";
    }
}
