package com.example.telecom.notification.service;

import com.example.telecom.notification.domain.NotificationMessage;
import com.example.telecom.notification.dto.SendResult;
import java.util.UUID;

/**
 * Email notification channel that handles sending email messages via simulated delivery.
 */
public class EmailNotificationChannel {

    private final String channelType = "EMAIL";
    private final NotificationChannelMetrics metrics;

    public EmailNotificationChannel() {
        this.metrics = new NotificationChannelMetrics();
    }

    public EmailNotificationChannel(NotificationChannelMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Validates and sends an email notification.
     *
     * @param message the notification message to send
     * @return the send result indicating success or failure
     */
    public SendResult send(NotificationMessage message) {
        if (!validateRecipient(message.getRecipient())) {
            metrics.incrementFailed(channelType);
            return SendResult.fail(channelType, "Invalid email recipient: " + message.getRecipient());
        }

        String messageId = "EMAIL-" + UUID.randomUUID().toString().substring(0, 8);
        long latency = 30L;

        try {
            // Simulate email sending
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            metrics.incrementFailed(channelType);
            return SendResult.fail(channelType, "Email send interrupted");
        }

        metrics.incrementSent(channelType);
        metrics.recordLatency(channelType, latency);
        return SendResult.ok(channelType, messageId);
    }

    /**
     * Validates an email address. Must contain '@' and '.', with non-empty local and domain parts.
     *
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    public boolean validateRecipient(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return false;
        }
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);
        if (localPart.isEmpty() || domainPart.isEmpty()) {
            return false;
        }
        int dotIndex = domainPart.indexOf('.');
        if (dotIndex <= 0 || dotIndex >= domainPart.length() - 1) {
            return false;
        }
        return true;
    }

    /**
     * Returns the channel type identifier.
     *
     * @return "EMAIL"
     */
    public String getChannelType() {
        return channelType;
    }

    public NotificationChannelMetrics getMetrics() {
        return metrics;
    }
}
