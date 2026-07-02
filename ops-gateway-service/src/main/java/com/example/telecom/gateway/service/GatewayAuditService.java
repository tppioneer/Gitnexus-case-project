package com.example.telecom.gateway.service;

import com.example.telecom.common.audit.AuditEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GatewayAuditService {

    private final List<AuditEntry> auditLog = new ArrayList<>();

    public void logDashboardAccess(String regionCode, String endpoint) {
        auditLog.add(new AuditEntry(
                UUID.randomUUID().toString(),
                "Dashboard",
                endpoint,
                "ACCESS",
                "system",
                "Dashboard accessed for region: " + regionCode,
                System.currentTimeMillis()
        ));
    }

    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
}
