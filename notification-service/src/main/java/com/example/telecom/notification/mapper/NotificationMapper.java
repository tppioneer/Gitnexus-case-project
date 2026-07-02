package com.example.telecom.notification.mapper;

import com.example.telecom.notification.dto.NotificationResponse;
import com.example.telecom.notification.template.NotificationRecord;

public class NotificationMapper {

    public NotificationResponse toResponse(NotificationRecord record) {
        NotificationResponse response = new NotificationResponse();
        response.setRecordId(record.getRecordId());
        response.setWorkOrderId(record.getWorkOrderId());
        response.setChannel(record.getChannel());
        response.setSubject(record.getSubject());
        response.setSent(record.isSent());
        response.setSentTime(record.getSentTime());
        return response;
    }
}
