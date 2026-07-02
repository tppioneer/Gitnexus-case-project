package com.example.telecom.notification;

import com.example.telecom.notification.channel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationChannelRegistryTest {

    private NotificationChannelRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new NotificationChannelRegistry(List.of(
                new SmsNotificationChannel(), new EmailNotificationChannel(), new WeComNotificationChannel()
        ));
    }

    @Test
    void shouldResolveSmsChannel() {
        assertNotNull(registry.resolve("SMS"));
    }

    @Test
    void shouldResolveEmailChannel() {
        assertNotNull(registry.resolve("EMAIL"));
    }

    @Test
    void shouldResolveWeComChannel() {
        assertNotNull(registry.resolve("WECOM"));
    }
}
