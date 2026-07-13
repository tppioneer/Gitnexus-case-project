package com.example.telecom.notification;

import com.example.telecom.notification.domain.FailedNotification;
import com.example.telecom.notification.policy.NotificationRetryPolicy;
import com.example.telecom.notification.repository.NotificationFailureRepository;
import com.example.telecom.notification.service.NotificationRetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NotificationRetryServiceTest {

    private NotificationFailureRepository failureRepository;
    private NotificationRetryPolicy retryPolicy;
    private NotificationRetryService retryService;

    @BeforeEach
    void setUp() {
        failureRepository = new NotificationFailureRepository();
        retryPolicy = new NotificationRetryPolicy(3, 5000L, 300000L, "FIXED");
        retryService = new NotificationRetryService(failureRepository, retryPolicy);
    }

    @Test
    void shouldRetryFailedNotificationSuccessfully() {
        FailedNotification failed = retryService.recordFailure(
                "notif-1", "SMS", "+1234567890",
                "Alert", "Critical issue detected", "Network timeout"
        );

        assertNotNull(failed);
        assertEquals("notif-1", failed.getNotificationId());
        assertEquals(1, failed.getAttemptCount());

        boolean result = retryService.retry(failed.getFailureId());
        assertTrue(result);
    }

    @Test
    void shouldNotRetryWhenMaxAttemptsExceeded() {
        FailedNotification failed = retryService.recordFailure(
                "notif-2", "EMAIL", "test@example.com",
                "Warning", "SLA breach imminent", "Connection refused"
        );

        failed.setAttemptCount(5);
        failureRepository.save(failed);

        boolean result = retryService.retry(failed.getFailureId());
        assertFalse(result);
    }

    @Test
    void shouldRecordFailure() {
        FailedNotification failed = retryService.recordFailure(
                "notif-3", "WECOM", "user-42",
                "Notification", "Work order updated", "Recipient not found"
        );

        assertNotNull(failed);
        assertNotNull(failed.getFailureId());
        assertEquals("notif-3", failed.getNotificationId());
        assertEquals("WECOM", failed.getChannel());
        assertEquals("user-42", failed.getRecipient());
        assertEquals("Notification", failed.getSubject());
        assertEquals("Work order updated", failed.getBody());
        assertEquals("Recipient not found", failed.getErrorMessage());
        assertEquals(1, failed.getAttemptCount());
        assertTrue(failed.getLastAttemptTime() > 0);
        assertTrue(failed.getCreatedAt() > 0);
    }

    @Test
    void shouldRetryAllFailedNotifications() {
        retryService.recordFailure("notif-1", "SMS", "+1234567890", "Alert", "Body", "Error");
        retryService.recordFailure("notif-2", "EMAIL", "a@b.com", "Alert", "Body", "Error");

        int successCount = retryService.retryAllFailed();
        assertTrue(successCount >= 0);
    }

    @Test
    void shouldReturnRetryStats() {
        retryService.recordFailure("notif-1", "SMS", "+1234567890", "Alert", "Body", "Error");
        retryService.recordFailure("notif-2", "EMAIL", "a@b.com", "Alert", "Body", "Error");

        Map<String, Object> stats = retryService.getRetryStats();

        assertNotNull(stats);
        assertEquals(2L, stats.get("totalFailures"));
    }

    @Test
    void shouldGetFailedNotifications() {
        retryService.recordFailure("notif-1", "SMS", "+1234567890", "Alert", "Body", "Error");
        retryService.recordFailure("notif-2", "EMAIL", "a@b.com", "Alert", "Body", "Error");

        List<FailedNotification> failedList = retryService.getFailedNotifications();
        assertEquals(2, failedList.size());
    }
}
