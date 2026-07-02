package com.example.telecom.notification.service;

import com.example.telecom.notification.rule.NotificationRule;
import com.example.telecom.notification.repository.NotificationRuleRepository;

import java.util.List;
import java.util.UUID;

public class NotificationRuleService {

    private final NotificationRuleRepository ruleRepository;

    public NotificationRuleService(NotificationRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public NotificationRule createRule(String eventType, String channel, String recipientTemplate) {
        NotificationRule rule = new NotificationRule(
                UUID.randomUUID().toString(), eventType, channel, recipientTemplate, true);
        return ruleRepository.save(rule);
    }

    public List<NotificationRule> findMatchingRules(String eventType) {
        return ruleRepository.findByEventType(eventType);
    }

    public String evaluate(String eventType) {
        List<NotificationRule> matches = findMatchingRules(eventType);
        return "Notification rules matched: " + matches.size() + " for event type " + eventType;
    }
}
