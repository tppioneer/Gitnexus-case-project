package com.example.telecom.alarm.repository;

import com.example.telecom.alarm.domain.AlarmCorrelationRule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for managing alarm correlation rules.
 */
public class AlarmCorrelationRuleRepository {

    private final Map<String, AlarmCorrelationRule> rules = new ConcurrentHashMap<>();

    public AlarmCorrelationRule save(AlarmCorrelationRule rule) {
        Objects.requireNonNull(rule, "rule must not be null");
        Objects.requireNonNull(rule.getRuleId(), "ruleId must not be null");
        rules.put(rule.getRuleId(), rule);
        return rule;
    }

    public Optional<AlarmCorrelationRule> findById(String ruleId) {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        return Optional.ofNullable(rules.get(ruleId));
    }

    public List<AlarmCorrelationRule> findAll() {
        return new ArrayList<>(rules.values());
    }

    public List<AlarmCorrelationRule> findByEnabled(boolean enabled) {
        return rules.values().stream()
                .filter(r -> r.isEnabled() == enabled)
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
