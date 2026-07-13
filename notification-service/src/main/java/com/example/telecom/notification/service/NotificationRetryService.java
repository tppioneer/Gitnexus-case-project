package com.example.telecom.notification.service;

import com.example.telecom.notification.domain.FailedNotification;
import com.example.telecom.notification.domain.NotificationMessage;
import com.example.telecom.notification.dto.SendResult;
import com.example.telecom.notification.policy.NotificationRetryPolicy;
import com.example.telecom.notification.repository.NotificationFailureRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NotificationRetryService {

    private final NotificationFailureRepository failureRepository;
    private final NotificationRetryPolicy retryPolicy;

    public NotificationRetryService(NotificationFailureRepository failureRepository,
                                     NotificationRetryPolicy retryPolicy) {
        this.failureRepository = failureRepository;
        this.retryPolicy = retryPolicy;
    }

    public boolean retry(String notificationId) {
        Optional<FailedNotification> opt = failureRepository.findById(notificationId);
        if (opt.isEmpty()) {
            return false;
        }

        FailedNotification failed = opt.get();

        if (!shouldRetry(failed)) {
            return false;
        }

        NotificationMessage message = new NotificationMessage(
                UUID.randomUUID().toString(),
                failed.getChannel(),
                failed.getRecipient(),
                failed.getSubject(),
                failed.getBody(),
                "MEDIUM",
                System.currentTimeMillis()
        );

        SendResult result = simulateSend(message);

        if (result.isSuccess()) {
            failureRepository.delete(notificationId);
            return true;
        } else {
            failed.setAttemptCount(failed.getAttemptCount() + 1);
            failed.setLastAttemptTime(System.currentTimeMillis());
            failureRepository.save(failed);
            return false;
        }
    }

    public int retryAllFailed() {
        List<FailedNotification> allFailed = failureRepository.findAll();
        int successCount = 0;
        for (FailedNotification failed : allFailed) {
            try {
                if (retry(failed.getFailureId())) {
                    successCount++;
                }
            } catch (Exception e) {
                // Skip failures that can't be retried
            }
        }
        return successCount;
    }

    public FailedNotification recordFailure(String notificationId, String channel,
                                              String recipient, String subject,
                                              String body, String errorMessage) {
        FailedNotification failed = new FailedNotification(
                UUID.randomUUID().toString(),
                notificationId,
                channel,
                recipient,
                subject,
                body,
                errorMessage,
                1,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
        return failureRepository.save(failed);
    }

    public List<FailedNotification> getFailedNotifications() {
        return failureRepository.findAll();
    }

    public Map<String, Object> getRetryStats() {
        List<FailedNotification> allFailed = failureRepository.findAll();
        Map<String, Object> stats = new HashMap<>();

        long totalFailures = allFailed.size();
        long totalRetries = allFailed.stream()
                .mapToLong(FailedNotification::getAttemptCount)
                .sum();
        double averageAttempts = totalFailures > 0 ? (double) totalRetries / totalFailures : 0.0;

        stats.put("totalFailures", totalFailures);
        stats.put("totalRetries", totalRetries);
        stats.put("averageAttempts", averageAttempts);
        stats.put("maxRetries", (long) retryPolicy.getMaxRetries());
        stats.put("retryStrategy", retryPolicy.getRetryStrategy());

        long retryableCount = allFailed.stream()
                .filter(this::shouldRetry)
                .count();
        stats.put("retryableCount", retryableCount);

        long expiredCount = allFailed.stream()
                .filter(f -> !shouldRetry(f))
                .count();
        stats.put("expiredCount", expiredCount);

        return stats;
    }

    private boolean shouldRetry(FailedNotification notification) {
        return retryPolicy.shouldRetry(notification);
    }

    private SendResult simulateSend(NotificationMessage message) {
        if (message.getRecipient() != null && !message.getRecipient().trim().isEmpty()) {
            String messageId = "RETRY-" + UUID.randomUUID().toString().substring(0, 8);
            return SendResult.ok(message.getChannel(), messageId);
        }
        return SendResult.fail(message.getChannel(), "Invalid recipient");
    }
}
