package com.example.telecom.notification;

import com.example.telecom.common.audit.AuditEntry;
import com.example.telecom.notification.service.NotificationAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationAuditServiceTest {

    private NotificationAuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new NotificationAuditService();
    }

    @Test
    void shouldLogNotificationSent() {
        auditService.logNotificationSent("rec-1", "SMS", "wo-1");
        List<AuditEntry> log = auditService.getAuditLog();
        assertEquals(1, log.size());
        assertEquals("NotificationRecord", log.get(0).getEntityType());
        assertEquals("SEND", log.get(0).getAction());
    }

    @Test
    void shouldTrackMultipleNotifications() {
        auditService.logNotificationSent("rec-1", "SMS", "wo-1");
        auditService.logNotificationSent("rec-2", "EMAIL", "wo-1");
        auditService.logNotificationSent("rec-3", "WECOM", "wo-2");
        assertEquals(3, auditService.getAuditLog().size());
    }

    @Test
    void shouldInitiallyBeEmpty() {
        assertTrue(auditService.getAuditLog().isEmpty());
    }
}
