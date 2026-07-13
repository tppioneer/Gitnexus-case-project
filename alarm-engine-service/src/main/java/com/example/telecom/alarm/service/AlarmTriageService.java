package com.example.telecom.alarm.service;

import com.example.telecom.alarm.domain.AlarmTriageRule;
import com.example.telecom.alarm.repository.AlarmTriageRuleRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for triaging alarms based on configured triage rules.
 * Classifies alarms into categories, assigns priorities, and suggests
 * appropriate actions for each alarm.
 */
public class AlarmTriageService {

    private final AlarmTriageRuleRepository triageRuleRepository;
    private final Map<String, TriageResult> triageResults = new HashMap<>();

    public AlarmTriageService(AlarmTriageRuleRepository triageRuleRepository) {
        this.triageRuleRepository = triageRuleRepository;
    }

    /**
     * Classifies an alarm into a category based on matching triage rules.
     * Returns the category name, or "UNCLASSIFIED" if no rule matches.
     */
    public String classify(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");
        List<AlarmTriageRule> matched = matchRules(alarm);
        if (matched.isEmpty()) {
            return "UNCLASSIFIED";
        }
        return matched.get(0).getCategory();
    }

    /**
     * Performs full triage on an alarm: classifies it, determines priority,
     * and suggests an action. Stores the result for later retrieval.
     */
    public TriageResult triage(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");
        List<AlarmTriageRule> matched = matchRules(alarm);

        String category = "UNCLASSIFIED";
        int priority = 5;
        String suggestedAction = "Review alarm details";
        String ruleName = null;

        if (!matched.isEmpty()) {
            AlarmTriageRule bestRule = matched.get(0);
            category = bestRule.getCategory();
            priority = bestRule.getPriority();
            ruleName = bestRule.getName();
            if (bestRule.getAutoAction() != null && !bestRule.getAutoAction().isEmpty()) {
                suggestedAction = bestRule.getAutoAction();
            } else {
                suggestedAction = buildDefaultAction(alarm, category);
            }
        }

        TriageResult result = new TriageResult(
                alarm.getAlarmId(),
                category,
                priority,
                suggestedAction,
                ruleName,
                System.currentTimeMillis()
        );
        triageResults.put(alarm.getAlarmId(), result);
        return result;
    }

    /**
     * Suggests an action for an alarm based on its severity and status.
     */
    public String suggestAction(AlarmRecord alarm) {
        Objects.requireNonNull(alarm, "alarm must not be null");
        if (alarm.getStatus() == AlarmStatus.CLEARED) {
            return "No action needed, alarm is already cleared";
        }
        if (alarm.getSeverity() == Severity.CRITICAL) {
            return "Immediate escalation required, notify on-call engineer";
        }
        if (alarm.getSeverity() == Severity.MAJOR) {
            return "Schedule investigation within 1 hour";
        }
        if (alarm.getSeverity() == Severity.WARNING) {
            return "Monitor and review during next maintenance window";
        }
        return "Log for informational purposes";
    }

    /**
     * Retrieves the stored triage result for an alarm.
     */
    public Optional<TriageResult> getTriageResult(String alarmId) {
        Objects.requireNonNull(alarmId, "alarmId must not be null");
        return Optional.ofNullable(triageResults.get(alarmId));
    }

    /**
     * Matches an alarm against all enabled triage rules, returning rules
     * that match sorted by priority (lower number = higher priority).
     */
    private List<AlarmTriageRule> matchRules(AlarmRecord alarm) {
        return triageRuleRepository.findByEnabled(true).stream()
                .filter(rule -> evaluateCondition(rule, alarm))
                .sorted(Comparator.comparingInt(AlarmTriageRule::getPriority))
                .collect(Collectors.toList());
    }

    /**
     * Evaluates the condition expression of a triage rule against an alarm.
     * Supports simple conditions: severity=X, status=Y, metricType=Z.
     */
    private boolean evaluateCondition(AlarmTriageRule rule, AlarmRecord alarm) {
        String expr = rule.getConditionExpression();
        if (expr == null || expr.isEmpty()) {
            return true;
        }
        String[] parts = expr.split("=", 2);
        if (parts.length != 2) {
            return false;
        }
        String field = parts[0].trim().toLowerCase();
        String value = parts[1].trim().toUpperCase();
        switch (field) {
            case "severity":
                return alarm.getSeverity() != null && alarm.getSeverity().name().equals(value);
            case "status":
                return alarm.getStatus() != null && alarm.getStatus().name().equals(value);
            case "metrictype":
                return alarm.getMetricType() != null && alarm.getMetricType().toUpperCase().contains(value);
            default:
                return false;
        }
    }

    private String buildDefaultAction(AlarmRecord alarm, String category) {
        return "Alarm classified as " + category + " for device " + alarm.getDeviceId()
                + " with severity " + alarm.getSeverity();
    }

    /**
     * Represents the result of a triage operation for a single alarm.
     */
    public static class TriageResult {
        private final String alarmId;
        private final String category;
        private final int priority;
        private final String suggestedAction;
        private final String matchedRuleName;
        private final long triageTime;

        public TriageResult(String alarmId, String category, int priority,
                             String suggestedAction, String matchedRuleName, long triageTime) {
            this.alarmId = alarmId;
            this.category = category;
            this.priority = priority;
            this.suggestedAction = suggestedAction;
            this.matchedRuleName = matchedRuleName;
            this.triageTime = triageTime;
        }

        public String getAlarmId() { return alarmId; }
        public String getCategory() { return category; }
        public int getPriority() { return priority; }
        public String getSuggestedAction() { return suggestedAction; }
        public String getMatchedRuleName() { return matchedRuleName; }
        public long getTriageTime() { return triageTime; }

        @Override
        public String toString() {
            return "TriageResult{alarmId='" + alarmId + "', category='" + category
                    + "', priority=" + priority + "}";
        }
    }
}
