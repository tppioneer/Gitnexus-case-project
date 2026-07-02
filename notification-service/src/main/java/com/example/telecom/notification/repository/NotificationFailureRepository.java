package com.example.telecom.notification.repository;

import com.example.telecom.notification.template.NotificationRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationFailureRepository {
    private final Map<String, NotificationRecord> failures = new ConcurrentHashMap<>();

    public NotificationRecord save(NotificationRecord record) {
        failures.put(record.getRecordId(), record);
        return record;
    }

    public List<NotificationRecord> findAll() {
        return new ArrayList<>(failures.values());
    }

    public int countFailures() {
        return failures.size();
    }
}
