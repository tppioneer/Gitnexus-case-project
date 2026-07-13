package com.example.telecom.notification.service;

import com.example.telecom.notification.domain.NotificationRule;
import com.example.telecom.notification.repository.NotificationRuleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationRuleService {

    private final NotificationRuleRepository ruleRepository;

    public NotificationRuleService(NotificationRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public NotificationRule createRule(String name, String userId, String channel,
                                        String eventType, String priorityThreshold,
                                        long timeWindow) {
        NotificationRule rule = new NotificationRule(
                UUID.randomUUID().toString(),
                name,
                userId,
                channel,
                eventType,
                priorityThreshold,
                true,
                timeWindow,
                LocalDateTime.now()
        );
        return ruleRepository.save(rule);
    }

    public NotificationRule updateRule(String ruleId, String name, String channel,
                                        String eventType, String priorityThreshold,
                                        long timeWindow, boolean enabled) {
        NotificationRule existing = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("NotificationRule not found: " + ruleId));
        existing.setName(name);
        existing.setChannel(channel);
        existing.setEventType(eventType);
        existing.setPriorityThreshold(priorityThreshold);
        existing.setTimeWindow(timeWindow);
        existing.setEnabled(enabled);
        return ruleRepository.save(existing);
    }

    public void deleteRule(String ruleId) {
        NotificationRule existing = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("NotificationRule not found: " + ruleId));
        ruleRepository.delete(ruleId);
    }

    public NotificationRule getRule(String ruleId) {
        return ruleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("NotificationRule not found: " + ruleId));
    }

    public List<NotificationRule> listRules() {
        return ruleRepository.findAll();
    }

    public List<NotificationRule> evaluate(String eventType, String userId, String priority) {
        return ruleRepository.findAll().stream()
                .filter(rule -> matchRule(rule, eventType, userId, priority))
                .collect(Collectors.toList());
    }

    private boolean matchRule(NotificationRule rule, String eventType, String userId, String priority) {
        if (!rule.isEnabled()) {
            return false;
        }
        if (!rule.getEventType().equals(eventType)) {
            return false;
        }
        if (rule.getUserId() != null && !rule.getUserId().isEmpty()
                && (userId == null || !rule.getUserId().equals(userId))) {
            return false;
        }
        if (rule.getPriorityThreshold() != null && !rule.getPriorityThreshold().isEmpty()
                && (priority == null || priority.compareTo(rule.getPriorityThreshold()) < 0)) {
            return false;
        }
        return true;
    }
}
