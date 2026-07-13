package com.example.telecom.alarm.service;

import com.example.telecom.alarm.domain.AlarmSuppressionRule;
import com.example.telecom.alarm.repository.AlarmSuppressionRuleRepository;
import com.example.telecom.alarm.rule.SuppressionRuleEvaluator;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.Severity;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Service that determines whether an alarm should be suppressed based on
 * configured suppression rules. Also maintains a record of suppressed alarms.
 */
public class AlarmSuppressionService {

    private final AlarmSuppressionRuleRepository suppressionRuleRepository;
    private final SuppressionRuleEvaluator suppressionRuleEvaluator;
    private final List<AlarmRecord> suppressedAlarms = new CopyOnWriteArrayList<>();

    public AlarmSuppressionService(AlarmSuppressionRuleRepository suppressionRuleRepository,
                                    SuppressionRuleEvaluator suppressionRuleEvaluator) {
        this.suppressionRuleRepository = suppressionRuleRepository;
        this.suppressionRuleEvaluator = suppressionRuleEvaluator;
    }

    /**
     * Determines whether an alarm should be suppressed by evaluating all
     * matching suppression rules.
     */
    public boolean shouldSuppress(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");
        List<AlarmSuppressionRule> matchingRules = suppressionRuleRepository.findMatchingRules(
                alarm.getDeviceId(), alarm.getMetricType());
        if (matchingRules.isEmpty()) {
            return false;
        }
        for (AlarmSuppressionRule rule : matchingRules) {
            if (suppressionRuleEvaluator.evaluate(alarm, rule)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Suppresses an alarm, adding it to the suppressed list and returning a
     * suppression result message.
     */
    public String suppress(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");
        suppressedAlarms.add(alarm);
        return "SUPPRESSED: Alarm " + alarm.getAlarmId() + " for device " + alarm.getDeviceId()
                + " has been suppressed by rule evaluation";
    }

    /**
     * Adds a new suppression rule.
     */
    public AlarmSuppressionRule addSuppressionRule(AlarmSuppressionRule rule) {
        Objects.requireNonNull(rule, "rule must not be null");
        return suppressionRuleRepository.save(rule);
    }

    /**
     * Removes a suppression rule by its ID.
     */
    public boolean removeSuppressionRule(String ruleId) {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        return suppressionRuleRepository.delete(ruleId);
    }

    /**
     * Lists all configured suppression rules.
     */
    public List<AlarmSuppressionRule> listSuppressionRules() {
        return suppressionRuleRepository.findAll();
    }

    /**
     * Returns the list of alarms that have been suppressed.
     */
    public List<AlarmRecord> getSuppressedAlarms() {
        return new ArrayList<>(suppressedAlarms);
    }

    /**
     * Evaluates alarm against all matching rules. Returns true if any rule
     * indicates suppression is needed.
     */
    private boolean evaluateRules(AlarmRecord alarm) {
        List<AlarmSuppressionRule> rules = suppressionRuleRepository.findMatchingRules(
                alarm.getDeviceId(), alarm.getMetricType());
        return rules.stream().anyMatch(rule -> suppressionRuleEvaluator.evaluate(alarm, rule));
    }
}
