package com.example.telecom.notification.service;

import com.example.telecom.notification.channel.NotificationChannel;
import com.example.telecom.notification.channel.NotificationChannelRegistry;
import com.example.telecom.notification.dto.NotificationRequest;
import com.example.telecom.notification.dto.SendResult;
import com.example.telecom.notification.repository.NotificationFailureRepository;
import com.example.telecom.notification.template.NotificationRecord;

import java.util.UUID;

public class NotificationRetryService {

    private final NotificationChannelRegistry channelRegistry;
    private final NotificationFailureRepository failureRepository;
    private final int maxRetries;

    public NotificationRetryService(NotificationChannelRegistry channelRegistry,
                                     NotificationFailureRepository failureRepository,
                                     int maxRetries) {
        this.channelRegistry = channelRegistry;
        this.failureRepository = failureRepository;
        this.maxRetries = maxRetries;
    }

    public boolean retry(NotificationRecord failedRecord, int attemptNumber) {
        if (attemptNumber > maxRetries) {
            failureRepository.save(failedRecord);
            return false;
        }
        NotificationRequest request = new NotificationRequest();
        request.setWorkOrderId(failedRecord.getWorkOrderId());
        request.setChannel(failedRecord.getChannel());
        request.setSubject(failedRecord.getSubject());
        request.setBody(failedRecord.getBody());
        request.setRecipient(failedRecord.getRecipient());

        NotificationChannel channel = channelRegistry.resolve(failedRecord.getChannel());
        SendResult result = channel.send(request);
        return result.isSuccess();
    }

    public void process(NotificationRecord failedRecord) {
        for (int i = 0; i < maxRetries; i++) {
            if (retry(failedRecord, i + 1)) return;
        }
    }

    public int getFailureCount() {
        return failureRepository.countFailures();
    }
}
