package com.example.telecom.notification;

import com.example.telecom.common.workorder.*;
import com.example.telecom.notification.channel.*;
import com.example.telecom.notification.consumer.WorkOrderEventConsumer;
import com.example.telecom.notification.repository.NotificationRepository;
import com.example.telecom.notification.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderEventConsumerTest {

    private WorkOrderEventConsumer consumer;
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository = new NotificationRepository();
        NotificationChannelRegistry channelRegistry = new NotificationChannelRegistry(List.of(
                new SmsNotificationChannel(), new EmailNotificationChannel(), new WeComNotificationChannel()
        ));
        NotificationTemplateService templateService = new NotificationTemplateService();
        NotificationService notificationService = new NotificationService(
                channelRegistry, templateService, notificationRepository);

        consumer = new WorkOrderEventConsumer(notificationService);
    }

    @Test
    void shouldConsumeWorkOrderEventAndSendNotification() {
        WorkOrderEvent event = new WorkOrderEvent("e1", "wo-1", WorkOrderStatus.PROCESSING,
                WorkOrderStatus.ESCALATED, "op-1", "EAST", System.currentTimeMillis());

        consumer.onWorkOrderChanged(event);
        assertFalse(notificationRepository.findByWorkOrderId("wo-1").isEmpty());
    }
}
