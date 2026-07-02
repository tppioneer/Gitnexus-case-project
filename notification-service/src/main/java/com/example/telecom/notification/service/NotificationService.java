package com.example.telecom.notification.service;

import com.example.telecom.common.workorder.WorkOrderEvent;
import com.example.telecom.notification.channel.NotificationChannel;
import com.example.telecom.notification.channel.NotificationChannelRegistry;
import com.example.telecom.notification.dto.NotificationRequest;
import com.example.telecom.notification.dto.SendResult;
import com.example.telecom.notification.repository.NotificationRepository;
import com.example.telecom.notification.template.NotificationRecord;

import java.util.UUID;

public class NotificationService {

    private final NotificationChannelRegistry channelRegistry;
    private final NotificationTemplateService templateService;
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationChannelRegistry channelRegistry,
                                NotificationTemplateService templateService,
                                NotificationRepository notificationRepository) {
        this.channelRegistry = channelRegistry;
        this.templateService = templateService;
        this.notificationRepository = notificationRepository;
    }

    public void process(WorkOrderEvent event) {
        String subject = templateService.renderSubject(event);
        String body = templateService.renderBody(event);
        String channel = determineChannel(event);

        NotificationRequest request = new NotificationRequest();
        request.setWorkOrderId(event.getWorkOrderId());
        request.setChannel(channel);
        request.setSubject(subject);
        request.setBody(body);
        request.setRecipient("operator@telecom.local");

        NotificationChannel notificationChannel = channelRegistry.resolve(channel);
        SendResult result = notificationChannel.send(request);

        NotificationRecord record = new NotificationRecord(
                UUID.randomUUID().toString(),
                event.getWorkOrderId(),
                channel,
                request.getRecipient(),
                subject,
                body,
                result.isSuccess(),
                System.currentTimeMillis()
        );
        notificationRepository.save(record);
    }

    private String determineChannel(WorkOrderEvent event) {
        return switch (event.getToStatus()) {
            case ESCALATED -> "SMS";
            case CLOSED, RESOLVED -> "EMAIL";
            default -> "WECOM";
        };
    }
}
