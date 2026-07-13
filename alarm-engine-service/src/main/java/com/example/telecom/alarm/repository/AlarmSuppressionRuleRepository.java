package com.example.telecom.alarm.repository;

import com.example.telecom.alarm.domain.AlarmSuppressionRule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for managing alarm suppression rules.
 */
public class AlarmSuppressionRuleRepository {

    private final Map<String, AlarmSuppressionRule> rules = new ConcurrentHashMap<>();

    public AlarmSuppressionRule save(AlarmSuppressionRule rule) {
        Objects.requireNonNull(rule, "rule must not be null");
        Objects.requireNonNull(rule.getRuleId(), "ruleId must not be null");
        rules.put(rule.getRuleId(), rule);
        return rule;
    }

    public Optional<AlarmSuppressionRule> findById(String ruleId) {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        return Optional.ofNullable(rules.get(ruleId));
    }

    public List<AlarmSuppressionRule> findAll() {
        return new ArrayList<>(rules.values());
    }

    public List<AlarmSuppressionRule> findByEnabled(boolean enabled) {
        return rules.values().stream()
                .filter(r -> r.isEnabled() == enabled)
                .collect(Collectors.toList());
    }

    /**
     * Finds matching suppression rules for a given device ID and alarm type.
     * A rule matches if its devicePattern is contained in the deviceId,
     * its alarmTypePattern is contained in the alarmType, and the rule is enabled.
     */
    public List<AlarmSuppressionRule> findMatchingRules(String deviceId, String alarmType) {
        Objects.requireNonNull(deviceId, "deviceId must not be null");
        Objects.requireNonNull(alarmType, "alarmType must not be null");
        return rules.values().stream()
                .filter(AlarmSuppressionRule::isEnabled)
                .filter(r -> deviceId.contains(r.getDevicePattern())
                        || r.getDevicePattern().equals("*")
                        || r.getDevicePattern().equalsIgnoreCase(deviceId))
                .filter(r -> alarmType.contains(r.getAlarmTypePattern())
                        || r.getAlarmTypePattern().equals("*"))
                .collect(Collectors.toList());
    }

    public boolean delete(String ruleId) {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        return rules.remove(ruleId) != null;
    }

    public long count() {
        return rules.size();
    }
}
