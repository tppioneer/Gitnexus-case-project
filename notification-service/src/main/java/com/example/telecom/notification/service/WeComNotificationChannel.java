package com.example.telecom.notification.service;

import com.example.telecom.notification.domain.NotificationMessage;
import com.example.telecom.notification.dto.SendResult;
import java.util.UUID;

/**
 * WeCom (WeChat for Business) notification channel that handles sending messages
 * to users via simulated delivery.
 */
public class WeComNotificationChannel {

    private final String channelType = "WECOM";
    private final NotificationChannelMetrics metrics;

    public WeComNotificationChannel() {
        this.metrics = new NotificationChannelMetrics();
    }

    public WeComNotificationChannel(NotificationChannelMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Validates and sends a WeCom notification.
     *
     * @param message the notification message to send
     * @return the send result indicating success or failure
     */
    public SendResult send(NotificationMessage message) {
        if (!validateRecipient(message.getRecipient())) {
            metrics.incrementFailed(channelType);
            return SendResult.fail(channelType, "Invalid WeCom recipient: " + message.getRecipient());
        }

        String messageId = "WECOM-" + UUID.randomUUID().toString().substring(0, 8);
        long latency = 40L;

        try {
            // Simulate WeCom message sending
            Thread.sleep(8);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            metrics.incrementFailed(channelType);
            return SendResult.fail(channelType, "WeCom send interrupted");
        }

        metrics.incrementSent(channelType);
        metrics.recordLatency(channelType, latency);
        return SendResult.ok(channelType, messageId);
    }

    /**
     * Validates a user ID. Must be non-null and non-empty.
     *
     * @param userId the user ID to validate
     * @return true if the user ID is valid, false otherwise
     */
    public boolean validateRecipient(String userId) {
        return userId != null && !userId.isEmpty();
    }

    /**
     * Returns the channel type identifier.
     *
     * @return "WECOM"
     */
    public String getChannelType() {
        return channelType;
    }

    public NotificationChannelMetrics getMetrics() {
        return metrics;
    }
}
