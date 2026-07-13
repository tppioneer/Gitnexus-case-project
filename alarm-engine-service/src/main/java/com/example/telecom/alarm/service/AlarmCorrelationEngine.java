package com.example.telecom.alarm.service;

import com.example.telecom.alarm.domain.AlarmCorrelationRule;
import com.example.telecom.alarm.repository.AlarmCorrelationRuleRepository;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Engine that correlates new alarms with existing alarms based on
 * configured correlation rules. Builds correlation groups and scores
 * correlation strength between alarms.
 */
public class AlarmCorrelationEngine {

    private final AlarmRepository alarmRepository;
    private final AlarmCorrelationRuleRepository correlationRuleRepository;
    private final Map<String, List<String>> correlationGroups = new HashMap<>();

    public AlarmCorrelationEngine(AlarmRepository alarmRepository,
                                   AlarmCorrelationRuleRepository correlationRuleRepository) {
        this.alarmRepository = alarmRepository;
        this.correlationRuleRepository = correlationRuleRepository;
    }

    /**
     * Correlates a new alarm with existing active alarms using the configured rules.
     * Returns the list of correlated alarms.
     */
    public List<AlarmRecord> correlate(AlarmRecord newAlarm) {
        Objects.requireNonNull(newAlarm, "newAlarm must not be null");
        List<AlarmRecord> correlated = findCorrelatedAlarms(newAlarm);
        if (!correlated.isEmpty()) {
            String groupId = buildCorrelationGroup(newAlarm, correlated);
            mergeCorrelated(newAlarm, correlated, groupId);
        }
        return correlated;
    }

    /**
     * Finds alarms that are correlated with the new alarm based on rules.
     * Uses device ID matching, alarm type patterns, severity ranges, and time windows.
     */
    public List<AlarmRecord> findCorrelatedAlarms(AlarmRecord newAlarm) {
        List<AlarmCorrelationRule> enabledRules = correlationRuleRepository.findByEnabled(true);
        if (enabledRules.isEmpty()) {
            return List.of();
        }

        List<AlarmRecord> activeAlarms = alarmRepository.findActiveAlarms().stream()
                .filter(a -> !a.getAlarmId().equals(newAlarm.getAlarmId()))
                .collect(Collectors.toList());

        if (activeAlarms.isEmpty()) {
            return List.of();
        }

        Set<String> correlatedIds = new HashSet<>();
        List<AlarmRecord> result = new ArrayList<>();

        for (AlarmRecord existing : activeAlarms) {
            for (AlarmCorrelationRule rule : enabledRules) {
                if (correlatedIds.contains(existing.getAlarmId())) {
                    continue;
                }
                double score = scoreCorrelation(newAlarm, existing, rule);
                if (score > 0.5) {
                    correlatedIds.add(existing.getAlarmId());
                    result.add(existing);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Builds a correlation group containing the new alarm and its correlated alarms.
     * Returns the group ID.
     */
    public String buildCorrelationGroup(AlarmRecord newAlarm, List<AlarmRecord> correlated) {
        String groupId = "CORR-GRP-" + UUID.randomUUID().toString().substring(0, 8);
        List<String> memberIds = new ArrayList<>();
        memberIds.add(newAlarm.getAlarmId());
        for (AlarmRecord corr : correlated) {
            memberIds.add(corr.getAlarmId());
        }
        correlationGroups.put(groupId, memberIds);
        return groupId;
    }

    /**
     * Merges the correlated alarms into the new alarm by setting correlation metadata.
     */
    public void mergeCorrelated(AlarmRecord newAlarm, List<AlarmRecord> correlated, String groupId) {
        newAlarm.setDescription(newAlarm.getDescription() + " [correlated:" + groupId
                + ", members:" + correlated.size() + "]");
        alarmRepository.save(newAlarm);
        for (AlarmRecord corr : correlated) {
            corr.setDescription(corr.getDescription() != null
                    ? corr.getDescription() + " [correlated:" + groupId + "]"
                    : "[correlated:" + groupId + "]");
            alarmRepository.save(corr);
        }
    }

    /**
     * Calculates a correlation score between two alarms based on the rule criteria.
     * Returns a value between 0.0 (no correlation) and 1.0 (strong correlation).
     */
    private double scoreCorrelation(AlarmRecord newAlarm, AlarmRecord existing, AlarmCorrelationRule rule) {
        double score = 0.0;
        int factors = 0;

        if (rule.getDeviceMatch() != null) {
            factors++;
            if (newAlarm.getDeviceId() != null && existing.getDeviceId() != null) {
                if (newAlarm.getDeviceId().equals(existing.getDeviceId())) {
                    score += 0.4;
                } else if (existing.getDeviceId().contains(rule.getDeviceMatch())
                        || newAlarm.getDeviceId().contains(rule.getDeviceMatch())) {
                    score += 0.2;
                }
            }
        }

        if (rule.getAlarmTypeMatch() != null) {
            factors++;
            if (newAlarm.getMetricType() != null && existing.getMetricType() != null) {
                if (newAlarm.getMetricType().equals(existing.getMetricType())) {
                    score += 0.3;
                } else if (existing.getMetricType().contains(rule.getAlarmTypeMatch())
                        || newAlarm.getMetricType().contains(rule.getAlarmTypeMatch())) {
                    score += 0.15;
                }
            }
        }

        if (rule.getSeverityLow() != null && rule.getSeverityHigh() != null) {
            factors++;
            if (isSeverityInRange(newAlarm.getSeverity(), rule.getSeverityLow(), rule.getSeverityHigh())
                    && isSeverityInRange(existing.getSeverity(), rule.getSeverityLow(), rule.getSeverityHigh())) {
                score += 0.2;
            }
        }

        if (rule.getTimeWindowMinutes() > 0) {
            factors++;
            long windowMillis = rule.getTimeWindowMinutes() * 60 * 1000;
            long timeDiff = Math.abs(newAlarm.getCreatedTime() - existing.getCreatedTime());
            if (timeDiff <= windowMillis) {
                score += 0.1;
            }
        }

        if (factors == 0) {
            return 0.0;
        }
        return score;
    }

    private boolean isSeverityInRange(Severity severity, Severity low, Severity high) {
        if (severity == null) {
            return false;
        }
        int ordinal = severity.ordinal();
        return ordinal >= low.ordinal() && ordinal <= high.ordinal();
    }

    /**
     * Returns all correlation groups.
     */
    public Map<String, List<String>> getCorrelationGroups() {
        return new HashMap<>(correlationGroups);
    }
}
