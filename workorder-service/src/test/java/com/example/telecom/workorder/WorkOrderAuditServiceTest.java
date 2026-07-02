package com.example.telecom.workorder;

import com.example.telecom.common.audit.AuditEntry;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.service.WorkOrderAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderAuditServiceTest {

    private WorkOrderAuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new WorkOrderAuditService();
    }

    @Test
    void shouldLogTransition() {
        auditService.logTransition("wo-1", WorkOrderStatus.CREATED, WorkOrderStatus.ASSIGNED, "operator-1");
        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(1, log.size());

        AuditEntry entry = log.get(0);
        assertEquals("WorkOrder", entry.getEntityType());
        assertEquals("wo-1", entry.getEntityId());
        assertEquals("TRANSITION", entry.getAction());
        assertTrue(entry.getDetails().contains("CREATED"));
        assertTrue(entry.getDetails().contains("ASSIGNED"));
    }

    @Test
    void shouldTrackMultipleTransitions() {
        auditService.logTransition("wo-1", WorkOrderStatus.CREATED, WorkOrderStatus.ASSIGNED, "op1");
        auditService.logTransition("wo-1", WorkOrderStatus.ASSIGNED, WorkOrderStatus.PROCESSING, "op2");
        auditService.logTransition("wo-2", WorkOrderStatus.PROCESSING, WorkOrderStatus.RESOLVED, "op1");
        assertEquals(3, auditService.getAuditLog().size());
    }

    @Test
    void shouldInitiallyBeEmpty() {
        assertTrue(auditService.getAuditLog().isEmpty());
    }

    @Test
    void shouldLogEscalationTransition() {
        auditService.logTransition("wo-escalated", WorkOrderStatus.PROCESSING,
                WorkOrderStatus.ESCALATED, "system");
        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(1, log.size());
        assertTrue(log.get(0).getDetails().contains("ESCALATED"));
    }

    @Test
    void shouldLogCancellationTransition() {
        auditService.logTransition("wo-cancel", WorkOrderStatus.ASSIGNED,
                WorkOrderStatus.CANCELLED, "admin");
        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(1, log.size());
        assertTrue(log.get(0).getDetails().contains("CANCELLED"));
    }
}
