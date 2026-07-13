package com.example.telecom.alarm.repository;

import com.example.telecom.alarm.domain.AlarmTriageRule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for managing alarm triage rules.
 */
public class AlarmTriageRuleRepository {

    private final Map<String, AlarmTriageRule> rules = new ConcurrentHashMap<>();

    public AlarmTriageRule save(AlarmTriageRule rule) {
        Objects.requireNonNull(rule, "rule must not be null");
        Objects.requireNonNull(rule.getRuleId(), "ruleId must not be null");
        rules.put(rule.getRuleId(), rule);
        return rule;
    }

    public Optional<AlarmTriageRule> findById(String ruleId) {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        return Optional.ofNullable(rules.get(ruleId));
    }

    public List<AlarmTriageRule> findAll() {
        return new ArrayList<>(rules.values());
    }

    public List<AlarmTriageRule> findByCategory(String category) {
        Objects.requireNonNull(category, "category must not be null");
        return rules.values().stream()
                .filter(r -> category.equals(r.getCategory()))
                .collect(Collectors.toList());
    }

    public List<AlarmTriageRule> findByEnabled(boolean enabled) {
        return rules.values().stream()
                .filter(r -> r.isEnabled() == enabled)
                .collect(Collectors.toList());
    }

    public boolean delete(String ruleId) {
        Objects.requireNonNull(ruleId, "ruleId must not be null");
        return rules.remove(ruleId) != null;
    }
}
