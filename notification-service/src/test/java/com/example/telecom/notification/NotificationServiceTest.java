package com.example.telecom.notification;

import com.example.telecom.common.workorder.*;
import com.example.telecom.notification.channel.*;
import com.example.telecom.notification.repository.NotificationRepository;
import com.example.telecom.notification.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private NotificationService notificationService;
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        NotificationChannelRegistry channelRegistry = new NotificationChannelRegistry(List.of(
                new SmsNotificationChannel(), new EmailNotificationChannel(), new WeComNotificationChannel()
        ));
        NotificationTemplateService templateService = new NotificationTemplateService();
        notificationRepository = new NotificationRepository();
        notificationService = new NotificationService(channelRegistry, templateService, notificationRepository);
    }

    @Test
    void shouldProcessWorkOrderEventAndCreateRecord() {
        WorkOrderEvent event = new WorkOrderEvent("e1", "wo-1", WorkOrderStatus.CREATED,
                WorkOrderStatus.ASSIGNED, "op-1", "EAST", System.currentTimeMillis());

        notificationService.process(event);
        assertEquals(1, notificationRepository.findByWorkOrderId("wo-1").size());
    }
}
