package com.example.telecom.notification.repository;

import com.example.telecom.notification.domain.FailedNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * In-memory repository for FailedNotification entities backed by ConcurrentHashMap.
 */
public class NotificationFailureRepository {

    private final ConcurrentMap<String, FailedNotification> store = new ConcurrentHashMap<>();

    /**
     * Saves a failed notification, keyed by its failureId. If a record with the
     * same ID already exists, it is replaced.
     *
     * @param notification the failed notification to save
     * @return the saved failed notification
     */
    public FailedNotification save(FailedNotification notification) {
        store.put(notification.getFailureId(), notification);
        return notification;
    }

    /**
     * Finds a failed notification by its unique failure identifier.
     *
     * @param failureId the failure ID to look up
     * @return an Optional containing the record if found, or empty if not
     */
    public Optional<FailedNotification> findById(String failureId) {
        return Optional.ofNullable(store.get(failureId));
    }

    /**
     * Returns all failed notification records currently stored.
     *
     * @return an unmodifiable list of all records
     */
    public List<FailedNotification> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    /**
     * Finds all failed notifications whose attempt count is less than the
     * specified maximum number of attempts (i.e., notifications that are
     * still eligible for retry).
     *
     * @param maxAttempts the maximum number of attempts threshold
     * @return a list of matching records (may be empty)
     */
    public List<FailedNotification> findByStatus(int maxAttempts) {
        return store.values().stream()
                .filter(n -> n.getAttemptCount() < maxAttempts)
                .collect(Collectors.toList());
    }

    /**
     * Finds all failed notifications that were created before the specified
     * expiry time (i.e., notifications that have expired and can be purged).
     *
     * @param expiryTime the epoch-millis threshold; records with createdAt
     *                   strictly less than this value are considered expired
     * @return a list of expired records (may be empty)
     */
    public List<FailedNotification> findExpired(long expiryTime) {
        return store.values().stream()
                .filter(n -> n.getCreatedAt() < expiryTime)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a failed notification record by its failure ID.
     *
     * @param failureId the ID of the record to remove
     */
    public void delete(String failureId) {
        store.remove(failureId);
    }

    /**
     * Returns the total number of stored failed notification records.
     *
     * @return the count of records in the repository
     */
    public long count() {
        return store.size();
    }
}
