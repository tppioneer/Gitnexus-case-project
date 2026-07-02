package com.example.telecom.alarm.service;

import com.example.telecom.common.audit.AuditEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlarmAuditService {

    private final List<AuditEntry> auditLog = new ArrayList<>();

    public void logAlarmCreated(String alarmId, String operator) {
        auditLog.add(new AuditEntry(
                UUID.randomUUID().toString(),
                "AlarmRecord",
                alarmId,
                "CREATE",
                operator,
                "Alarm created",
                System.currentTimeMillis()
        ));
    }

    public void logAlarmAcknowledged(String alarmId, String operator) {
        auditLog.add(new AuditEntry(
                UUID.randomUUID().toString(),
                "AlarmRecord",
                alarmId,
                "ACKNOWLEDGE",
                operator,
                "Alarm acknowledged",
                System.currentTimeMillis()
        ));
    }

    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}
