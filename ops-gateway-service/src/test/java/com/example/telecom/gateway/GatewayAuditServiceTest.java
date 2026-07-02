package com.example.telecom.gateway;

import com.example.telecom.common.audit.AuditEntry;
import com.example.telecom.gateway.service.GatewayAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GatewayAuditServiceTest {

    private GatewayAuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new GatewayAuditService();
    }

    @Test
    void shouldLogDashboardAccess() {
        auditService.logDashboardAccess("EAST", "/api/dashboard/overview");
        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(1, log.size());

        AuditEntry entry = log.get(0);
        assertEquals("Dashboard", entry.getEntityType());
        assertEquals("/api/dashboard/overview", entry.getEntityId());
        assertEquals("ACCESS", entry.getAction());
        assertTrue(entry.getDetails().contains("EAST"));
    }

    @Test
    void shouldTrackMultipleDashboardAccesses() {
        auditService.logDashboardAccess("EAST", "/api/dashboard/overview");
        auditService.logDashboardAccess("WEST", "/api/dashboard/devices/summary");
        auditService.logDashboardAccess("SOUTH", "/api/dashboard/alarms/summary");
        auditService.logDashboardAccess("NORTH", "/api/dashboard/workorders/summary");
        assertEquals(4, auditService.getAuditLog().size());
    }

    @Test
    void shouldInitiallyBeEmpty() {
        assertTrue(auditService.getAuditLog().isEmpty());
    }

    @Test
    void shouldLogDifferentEndpoints() {
        auditService.logDashboardAccess("EAST", "/api/dashboard/overview");
        auditService.logDashboardAccess("EAST", "/api/dashboard/health");

        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(2, log.size());
        assertEquals("/api/dashboard/overview", log.get(0).getEntityId());
        assertEquals("/api/dashboard/health", log.get(1).getEntityId());
    }
}
