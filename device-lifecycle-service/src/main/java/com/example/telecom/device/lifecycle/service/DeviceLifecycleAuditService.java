package com.example.telecom.device.lifecycle.service;

import com.example.telecom.device.lifecycle.model.AuditEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class DeviceLifecycleAuditService {

    private static final Logger log = LoggerFactory.getLogger(DeviceLifecycleAuditService.class);

    private final ConcurrentMap<String, List<AuditEntry>> auditStore = new ConcurrentHashMap<>();
    private final List<AuditEntry> allEntries = new ArrayList<>();

    public void recordAction(String deviceId, String action, String performedBy) {
        AuditEntry entry = createAuditEntry(deviceId, action, performedBy);
        auditStore.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(entry);
        allEntries.add(entry);
        log.debug("Audit recorded: device={}, action={}, by={}", deviceId, action, performedBy);
    }

    public List<AuditEntry> getHistory(String deviceId) {
        if (deviceId == null) {
            return List.of();
        }
        List<AuditEntry> entries = auditStore.get(deviceId);
        if (entries == null) {
            return List.of();
        }
        return entries.stream()
                .sorted(Comparator.comparing(AuditEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getHistoryByDateRange(String deviceId, LocalDateTime from, LocalDateTime to) {
        if (deviceId == null || from == null || to == null) {
            return List.of();
        }
        List<AuditEntry> entries = auditStore.get(deviceId);
        if (entries == null) {
            return List.of();
        }
        return entries.stream()
                .filter(e -> {
                    LocalDateTime ts = e.getTimestamp();
                    return ts != null && !ts.isBefore(from) && !ts.isAfter(to);
                })
                .sorted(Comparator.comparing(AuditEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getRecentActions(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return allEntries.stream()
                .sorted(Comparator.comparing(AuditEntry::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public long getAuditCount(String deviceId) {
        if (deviceId == null) {
            return 0;
        }
        List<AuditEntry> entries = auditStore.get(deviceId);
        return entries == null ? 0 : entries.size();
    }

    public long getTotalAuditCount() {
        return allEntries.size();
    }

    public void clearHistory(String deviceId) {
        if (deviceId != null) {
            auditStore.remove(deviceId);
        }
    }

    public List<AuditEntry> getActionsByPerformer(String performedBy) {
        if (performedBy == null) {
            return List.of();
        }
        return allEntries.stream()
                .filter(e -> performedBy.equals(e.getPerformedBy()))
                .sorted(Comparator.comparing(AuditEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<AuditEntry> getActionsByType(String action) {
        if (action == null) {
            return List.of();
        }
        return allEntries.stream()
                .filter(e -> action.equals(e.getAction()))
                .sorted(Comparator.comparing(AuditEntry::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    private AuditEntry createAuditEntry(String deviceId, String action, String performedBy) {
        String details = action + " operation on device " + deviceId + " by " + performedBy;
        return new AuditEntry(deviceId, action, performedBy, LocalDateTime.now(), details);
    }
}
