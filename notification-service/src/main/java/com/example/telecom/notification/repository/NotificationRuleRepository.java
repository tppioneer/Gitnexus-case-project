package com.example.telecom.notification.repository;

import com.example.telecom.notification.rule.NotificationRule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationRuleRepository {
    private final Map<String, NotificationRule> rules = new ConcurrentHashMap<>();

    public NotificationRule save(NotificationRule rule) {
        rules.put(rule.getRuleId(), rule);
        return rule;
    }

    public Optional<NotificationRule> findById(String ruleId) {
        return Optional.ofNullable(rules.get(ruleId));
    }

    public List<NotificationRule> findByEventType(String eventType) {
        return rules.values().stream()
                .filter(r -> r.matches(eventType))
                .toList();
    }

    public List<NotificationRule> findAll() {
        return new ArrayList<>(rules.values());
    }
}
