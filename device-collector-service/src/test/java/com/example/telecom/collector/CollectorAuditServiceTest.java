package com.example.telecom.collector;

import com.example.telecom.collector.service.CollectorAuditService;
import com.example.telecom.common.audit.AuditEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectorAuditServiceTest {

    private CollectorAuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new CollectorAuditService();
    }

    @Test
    void shouldLogMetricIngested() {
        auditService.logMetricIngested("dev-1", "metric-1", "system");
        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(1, log.size());

        AuditEntry entry = log.get(0);
        assertEquals("DeviceMetric", entry.getEntityType());
        assertEquals("metric-1", entry.getEntityId());
        assertEquals("INGEST", entry.getAction());
        assertTrue(entry.getDetails().contains("dev-1"));
    }

    @Test
    void shouldLogDeviceRegistered() {
        auditService.logDeviceRegistered("dev-2", "admin");
        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(1, log.size());

        AuditEntry entry = log.get(0);
        assertEquals("DeviceInfo", entry.getEntityType());
        assertEquals("REGISTER", entry.getAction());
    }

    @Test
    void shouldTrackMultipleEvents() {
        auditService.logDeviceRegistered("dev-1", "admin");
        auditService.logMetricIngested("dev-1", "m1", "system");
        auditService.logMetricIngested("dev-1", "m2", "system");
        auditService.logMetricIngested("dev-2", "m3", "system");

        assertEquals(4, auditService.getAuditLog().size());
    }

    @Test
    void shouldInitiallyBeEmpty() {
        assertTrue(auditService.getAuditLog().isEmpty());
    }
}
