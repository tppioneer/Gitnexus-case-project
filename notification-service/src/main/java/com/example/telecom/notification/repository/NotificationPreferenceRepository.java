package com.example.telecom.notification.repository;

import com.example.telecom.common.user.NotificationPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for NotificationPreference entities backed by ConcurrentHashMap.
 * Preferences are keyed by userId.
 */
public class NotificationPreferenceRepository {

    private final ConcurrentMap<String, NotificationPreference> store = new ConcurrentHashMap<>();

    /**
     * Saves a notification preference, keyed by its userId. If a preference
     * for the same user already exists, it is replaced.
     *
     * @param preference the preference to save
     * @return the saved preference
     */
    public NotificationPreference save(NotificationPreference preference) {
        store.put(preference.getUserId(), preference);
        return preference;
    }

    /**
     * Finds a notification preference by its user identifier.
     *
     * @param userId the user ID to look up
     * @return an Optional containing the preference if found, or empty if not
     */
    public Optional<NotificationPreference> findById(String userId) {
        return Optional.ofNullable(store.get(userId));
    }

    /**
     * Returns all notification preferences currently stored.
     *
     * @return an unmodifiable list of all preferences
     */
    public List<NotificationPreference> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    /**
     * Finds all notification preferences where the specified notification
     * channel is enabled.
     *
     * @param channel the channel name to check (e.g., "EMAIL", "SMS", "PUSH")
     * @return a list of matching preferences (may be empty)
     */
    public List<NotificationPreference> findByChannelEnabled(String channel) {
        return store.values().stream()
                .filter(pref -> pref.isChannelEnabled(channel))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a notification preference by its user ID.
     *
     * @param userId the user ID of the preference to remove
     */
    public void delete(String userId) {
        store.remove(userId);
    }

    /**
     * Returns the total number of stored notification preferences.
     *
     * @return the count of preferences in the repository
     */
    public long count() {
        return store.size();
    }
}
