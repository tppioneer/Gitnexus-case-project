package com.example.telecom.notification.service;

import com.example.telecom.common.audit.AuditEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationAuditService {

    private final List<AuditEntry> auditLog = new ArrayList<>();

    public void logNotificationSent(String recordId, String channel, String workOrderId) {
        auditLog.add(new AuditEntry(
                UUID.randomUUID().toString(),
                "NotificationRecord",
                recordId,
                "SEND",
                "system",
                "Notification sent via " + channel + " for work order " + workOrderId,
                System.currentTimeMillis()
        ));
    }

    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}
