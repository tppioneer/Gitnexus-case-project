package com.example.telecom.workorder.service;

import com.example.telecom.common.audit.AuditEntry;
import com.example.telecom.common.workorder.WorkOrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkOrderAuditService {

    private final List<AuditEntry> auditLog = new ArrayList<>();

    public void logTransition(String workOrderId, WorkOrderStatus from, WorkOrderStatus to, String operator) {
        auditLog.add(new AuditEntry(
                UUID.randomUUID().toString(),
                "WorkOrder",
                workOrderId,
                "TRANSITION",
                operator,
                "Status changed from " + from + " to " + to,
                System.currentTimeMillis()
        ));
    }

    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}
