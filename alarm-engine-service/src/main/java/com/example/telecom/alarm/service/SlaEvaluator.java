package com.example.telecom.alarm.service;

import com.example.telecom.common.alarm.AlarmRecord;

/**
 * SLA evaluation service. Has an evaluate() method but does NOT implement RuleEvaluator.
 * This is a Case B NOISE item — grep will find this evaluate(), but it should not be modified.
 */
public class SlaEvaluator {

    /**
     * Evaluate SLA compliance for an alarm lifecycle.
     * NOT related to RuleEvaluator.evaluate().
     */
    public boolean evaluate(AlarmRecord alarm, long slaThresholdMs) {
        long duration = System.currentTimeMillis() - alarm.getCreatedTime();
        return duration <= slaThresholdMs;
    }

    public String evaluate(String alarmId) {
        return "SLA evaluation for alarm: " + alarmId + " — compliant";
    }
}
