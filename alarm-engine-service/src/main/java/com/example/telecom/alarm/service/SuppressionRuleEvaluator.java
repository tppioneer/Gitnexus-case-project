package com.example.telecom.alarm.service;

import com.example.telecom.common.alarm.AlarmRecord;

import java.util.List;

/**
 * Evaluates whether an alarm should be suppressed.
 * Has evaluate() method but does NOT implement RuleEvaluator.
 * This is a Case B NOISE item.
 */
public class SuppressionRuleEvaluator {

    private final AlarmSuppressionService alarmSuppressionService;

    public SuppressionRuleEvaluator(AlarmSuppressionService alarmSuppressionService) {
        this.alarmSuppressionService = alarmSuppressionService;
    }

    public boolean evaluate(AlarmRecord alarm) {
        return alarmSuppressionService.shouldSuppress(alarm);
    }

    public String evaluate(List<AlarmRecord> alarms) {
        long suppressed = alarms.stream().filter(this::evaluate).count();
        return "Suppression evaluation: " + suppressed + "/" + alarms.size() + " alarms suppressed";
    }
}
