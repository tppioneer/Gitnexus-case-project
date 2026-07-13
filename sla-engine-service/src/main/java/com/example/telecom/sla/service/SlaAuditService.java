package com.example.telecom.sla.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class SlaAuditService {

    private static final Logger log = LoggerFactory.getLogger(SlaAuditService.class);

    private final Deque<SlaAuditEntry> auditLog = new ConcurrentLinkedDeque<>();

    public void recordAction(String action, String entityType, String entityId, String details, String performedBy) {
        SlaAuditEntry entry = new SlaAuditEntry(
                UUID.randomUUID().toString(),
                action,
                entityType,
                entityId,
                details,
                performedBy != null ? performedBy : "SYSTEM",
                LocalDateTime.now()
        );
        auditLog.addFirst(entry);
        log.info("Audit: action={}, entityType={}, entityId={}, by={}", action, entityType, entityId, performedBy);
    }

    public List<SlaAuditEntry> getHistory(int limit) {
        return auditLog.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<SlaAuditEntry> getHistoryByContract(String contractId) {
        return auditLog.stream()
                .filter(e -> contractId.equals(e.getEntityId()))
                .collect(Collectors.toList());
    }

    public List<SlaAuditEntry> getRecentActions(int minutes) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutes);
        return auditLog.stream()
                .filter(e -> e.getTimestamp().isAfter(cutoff))
                .collect(Collectors.toList());
    }

    public static class SlaAuditEntry {
        private final String auditId;
        private final String action;
        private final String entityType;
        private final String entityId;
        private final String details;
        private final String performedBy;
        private final LocalDateTime timestamp;

        public SlaAuditEntry(String auditId, String action, String entityType,
                             String entityId, String details, String performedBy,
                             LocalDateTime timestamp) {
            this.auditId = auditId;
            this.action = action;
            this.entityType = entityType;
            this.entityId = entityId;
            this.details = details;
            this.performedBy = performedBy;
            this.timestamp = timestamp;
        }

        public String getAuditId() { return auditId; }
        public String getAction() { return action; }
        public String getEntityType() { return entityType; }
        public String getEntityId() { return entityId; }
        public String getDetails() { return details; }
        public String getPerformedBy() { return performedBy; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
