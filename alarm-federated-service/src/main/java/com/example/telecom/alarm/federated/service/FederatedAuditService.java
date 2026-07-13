package com.example.telecom.alarm.federated.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class FederatedAuditService {

    private final ConcurrentLinkedDeque<AuditEntry> auditLog = new ConcurrentLinkedDeque<>();

    public void recordAction(String alarmId, String action, String performedBy) {
        AuditEntry entry = new AuditEntry(
                UUID.randomUUID().toString(),
                alarmId,
                action,
                performedBy != null ? performedBy : "SYSTEM",
                LocalDateTime.now()
        );
        auditLog.addFirst(entry);
    }

    public List<AuditEntry> getHistory(String alarmId) {
        return auditLog.stream()
                .filter(e -> alarmId.equals(e.getAlarmId()))
                .sorted(Comparator.comparing(AuditEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getRecentActions(int limit) {
        return auditLog.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getActionsByDateRange(LocalDateTime from, LocalDateTime to) {
        return auditLog.stream()
                .filter(e -> !e.getTimestamp().isBefore(from) && !e.getTimestamp().isAfter(to))
                .sorted(Comparator.comparing(AuditEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public static class AuditEntry {
        private final String id;
        private final String alarmId;
        private final String action;
        private final String performedBy;
        private final LocalDateTime timestamp;

        public AuditEntry(String id, String alarmId, String action, String performedBy, LocalDateTime timestamp) {
            this.id = id;
            this.alarmId = alarmId;
            this.action = action;
            this.performedBy = performedBy;
            this.timestamp = timestamp;
        }

        public String getId() {
            return id;
        }

        public String getAlarmId() {
            return alarmId;
        }

        public String getAction() {
            return action;
        }

        public String getPerformedBy() {
            return performedBy;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
