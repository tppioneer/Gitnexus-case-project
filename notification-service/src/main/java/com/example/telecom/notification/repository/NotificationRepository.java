package com.example.telecom.notification.repository;

import com.example.telecom.notification.template.NotificationRecord;
import com.example.telecom.notification.template.NotificationTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationRepository {
    private final Map<String, NotificationRecord> records = new ConcurrentHashMap<>();
    private final Map<String, NotificationTemplate> templates = new ConcurrentHashMap<>();

    public NotificationRecord save(NotificationRecord record) {
        records.put(record.getRecordId(), record);
        return record;
    }

    public Optional<NotificationRecord> findRecordById(String recordId) {
        return Optional.ofNullable(records.get(recordId));
    }

    public List<NotificationRecord> findByWorkOrderId(String workOrderId) {
        return records.values().stream()
                .filter(r -> workOrderId.equals(r.getWorkOrderId()))
                .toList();
    }

    public NotificationTemplate saveTemplate(NotificationTemplate template) {
        templates.put(template.getTemplateId(), template);
        return template;
    }

    public Optional<NotificationTemplate> findTemplateById(String templateId) {
        return Optional.ofNullable(templates.get(templateId));
    }
}
