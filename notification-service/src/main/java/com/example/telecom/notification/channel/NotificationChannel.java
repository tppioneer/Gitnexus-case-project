package com.example.telecom.notification.channel;

import com.example.telecom.notification.dto.NotificationRequest;
import com.example.telecom.notification.dto.SendResult;

public interface NotificationChannel {
    SendResult send(NotificationRequest request);
}
