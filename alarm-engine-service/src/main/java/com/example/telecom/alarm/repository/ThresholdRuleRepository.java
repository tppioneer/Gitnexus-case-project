package com.example.telecom.alarm.repository;

import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.MetricType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ThresholdRuleRepository {
    private final Map<String, ThresholdRule> rules = new ConcurrentHashMap<>();

    public ThresholdRule save(ThresholdRule rule) {
        rules.put(rule.getRuleId(), rule);
        return rule;
    }

    public Optional<ThresholdRule> findById(String ruleId) {
        return Optional.ofNullable(rules.get(ruleId));
    }

    public List<ThresholdRule> loadRules(MetricType metricType) {
        return rules.values().stream()
                .filter(r -> r.getMetricType().equals(metricType.name()))
                .filter(ThresholdRule::isEnabled)
                .toList();
    }

    public List<ThresholdRule> findAll() {
        return new ArrayList<>(rules.values());
    }
}
