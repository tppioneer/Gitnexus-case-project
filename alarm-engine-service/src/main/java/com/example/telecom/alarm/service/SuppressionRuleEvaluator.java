package com.example.telecom.alarm.service;

import com.example.telecom.alarm.domain.AlarmSuppressionRule;
import com.example.telecom.alarm.repository.AlarmSuppressionRuleRepository;
import com.example.telecom.common.alarm.AlarmRecord;

import java.util.List;

/**
 * Evaluates whether an alarm should be suppressed.
 * Has evaluate() method but does NOT implement RuleEvaluator.
 * This is a Case B NOISE item.
 */
public class SuppressionRuleEvaluator {

    private final AlarmSuppressionRuleRepository suppressionRuleRepository;

    public SuppressionRuleEvaluator(AlarmSuppressionRuleRepository suppressionRuleRepository) {
        this.suppressionRuleRepository = suppressionRuleRepository;
    }

    public boolean evaluate(AlarmRecord alarm) {
        return !suppressionRuleRepository.findMatchingRules(
                alarm.getDeviceId(), alarm.getMetricType()).isEmpty();
    }

    public String evaluate(List<AlarmRecord> alarms) {
        long suppressed = alarms.stream().filter(this::evaluate).count();
        return "Suppression evaluation: " + suppressed + "/" + alarms.size() + " alarms suppressed";
    }
}
