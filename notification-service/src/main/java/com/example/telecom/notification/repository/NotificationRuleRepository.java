package com.example.telecom.notification.repository;

import com.example.telecom.notification.domain.NotificationRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for NotificationRule entities backed by ConcurrentHashMap.
 */
public class NotificationRuleRepository {

    private final ConcurrentMap<String, NotificationRule> store = new ConcurrentHashMap<>();

    /**
     * Saves a notification rule, keyed by its ruleId. If a rule with the same
     * ID already exists, it is replaced.
     *
     * @param rule the rule to save
     * @return the saved rule
     */
    public NotificationRule save(NotificationRule rule) {
        store.put(rule.getRuleId(), rule);
        return rule;
    }

    /**
     * Finds a notification rule by its unique rule identifier.
     *
     * @param ruleId the rule ID to look up
     * @return an Optional containing the rule if found, or empty if not
     */
    public Optional<NotificationRule> findById(String ruleId) {
        return Optional.ofNullable(store.get(ruleId));
    }

    /**
     * Returns all notification rules currently stored.
     *
     * @return an unmodifiable list of all rules
     */
    public List<NotificationRule> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    /**
     * Finds all notification rules associated with a given user ID.
     *
     * @param userId the user ID to filter by
     * @return a list of matching rules (may be empty)
     */
    public List<NotificationRule> findByUserId(String userId) {
        return store.values().stream()
                .filter(rule -> rule.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Finds all notification rules that match a given event type.
     *
     * @param eventType the event type to filter by
     * @return a list of matching rules (may be empty)
     */
    public List<NotificationRule> findByEventType(String eventType) {
        return store.values().stream()
                .filter(rule -> rule.getEventType().equals(eventType))
                .collect(Collectors.toList());
    }

    /**
     * Finds all notification rules that are currently enabled.
     *
     * @return a list of enabled rules (may be empty)
     */
    public List<NotificationRule> findEnabled() {
        return store.values().stream()
                .filter(NotificationRule::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a notification rule by its rule ID.
     *
     * @param ruleId the ID of the rule to remove
     */
    public void delete(String ruleId) {
        store.remove(ruleId);
    }

    /**
     * Returns the total number of stored notification rules.
     *
     * @return the count of rules in the repository
     */
    public long count() {
        return store.size();
    }
}
