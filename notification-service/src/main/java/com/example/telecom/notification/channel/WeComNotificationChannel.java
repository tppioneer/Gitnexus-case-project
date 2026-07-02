package com.example.telecom.notification.channel;

import com.example.telecom.notification.dto.NotificationRequest;
import com.example.telecom.notification.dto.SendResult;

import java.util.UUID;

public class WeComNotificationChannel implements NotificationChannel {

    @Override
    public SendResult send(NotificationRequest request) {
        String messageId = "WECOM-" + UUID.randomUUID().toString().substring(0, 8);
        return SendResult.ok("WECOM", messageId);
    }
}
