package com.example.telecom.notification;

import com.example.telecom.notification.repository.NotificationRepository;
import com.example.telecom.notification.template.NotificationRecord;
import com.example.telecom.notification.template.NotificationTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationRepositoryTest {

    private NotificationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new NotificationRepository();
    }

    @Test
    void shouldSaveAndFindNotificationRecord() {
        NotificationRecord record = new NotificationRecord("r1", "wo-1", "SMS",
                "13800000001", "Subject", "Body", true, System.currentTimeMillis());
        repository.save(record);
        assertTrue(repository.findRecordById("r1").isPresent());
    }

    @Test
    void shouldFindByWorkOrderId() {
        repository.save(new NotificationRecord("r1", "wo-1", "SMS",
                "111", "S1", "B1", true, System.currentTimeMillis()));
        repository.save(new NotificationRecord("r2", "wo-1", "EMAIL",
                "a@b.com", "S2", "B2", true, System.currentTimeMillis()));
        repository.save(new NotificationRecord("r3", "wo-2", "SMS",
                "222", "S3", "B3", false, System.currentTimeMillis()));

        assertEquals(2, repository.findByWorkOrderId("wo-1").size());
        assertEquals(1, repository.findByWorkOrderId("wo-2").size());
        assertTrue(repository.findByWorkOrderId("wo-nonexistent").isEmpty());
    }

    @Test
    void shouldSaveAndFindTemplate() {
        NotificationTemplate template = new NotificationTemplate("t1", "SMS Template",
                "SMS", "Alert: ${workOrderId}", "Work order ${workOrderId} status changed");
        repository.saveTemplate(template);

        assertTrue(repository.findTemplateById("t1").isPresent());
        assertEquals("SMS Template", repository.findTemplateById("t1").get().getName());
    }

    @Test
    void shouldReturnEmptyForMissingRecord() {
        assertTrue(repository.findRecordById("nonexistent").isEmpty());
    }

    @Test
    void shouldReturnEmptyForMissingTemplate() {
        assertTrue(repository.findTemplateById("nonexistent").isEmpty());
    }
}
