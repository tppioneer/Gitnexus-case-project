package com.example.telecom.notification.channel;

import com.example.telecom.notification.dto.NotificationRequest;
import com.example.telecom.notification.dto.SendResult;

import java.util.UUID;

public class EmailNotificationChannel implements NotificationChannel {

    @Override
    public SendResult send(NotificationRequest request) {
        String messageId = "EMAIL-" + UUID.randomUUID().toString().substring(0, 8);
        return SendResult.ok("EMAIL", messageId);
    }
}
