package com.example.telecom.notification.service;

import com.example.telecom.notification.domain.NotificationMessage;
import com.example.telecom.notification.dto.SendResult;
import java.util.UUID;

/**
 * SMS notification channel that handles sending SMS messages via simulated delivery.
 */
public class SmsNotificationChannel {

    private final String channelType = "SMS";
    private final NotificationChannelMetrics metrics;

    public SmsNotificationChannel() {
        this.metrics = new NotificationChannelMetrics();
    }

    public SmsNotificationChannel(NotificationChannelMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Validates and sends an SMS notification.
     *
     * @param message the notification message to send
     * @return the send result indicating success or failure
     */
    public SendResult send(NotificationMessage message) {
        if (!validateRecipient(message.getRecipient())) {
            metrics.incrementFailed(channelType);
            return SendResult.fail(channelType, "Invalid SMS recipient: " + message.getRecipient());
        }

        String messageId = "SMS-" + UUID.randomUUID().toString().substring(0, 8);
        long latency = 50L;

        try {
            // Simulate SMS sending
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            metrics.incrementFailed(channelType);
            return SendResult.fail(channelType, "SMS send interrupted");
        }

        metrics.incrementSent(channelType);
        metrics.recordLatency(channelType, latency);
        return SendResult.ok(channelType, messageId);
    }

    /**
     * Validates a phone number. Must start with + or a digit, be at least 10 characters,
     * and contain only digits, '+', or '-' characters.
     *
     * @param phoneNumber the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public boolean validateRecipient(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        if (phoneNumber.length() < 10) {
            return false;
        }
        char firstChar = phoneNumber.charAt(0);
        if (firstChar != '+' && !Character.isDigit(firstChar)) {
            return false;
        }
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            if (!Character.isDigit(c) && c != '+' && c != '-') {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the channel type identifier.
     *
     * @return "SMS"
     */
    public String getChannelType() {
        return channelType;
    }

    public NotificationChannelMetrics getMetrics() {
        return metrics;
    }
}
