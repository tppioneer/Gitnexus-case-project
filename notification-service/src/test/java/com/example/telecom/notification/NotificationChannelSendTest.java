package com.example.telecom.notification;

import com.example.telecom.notification.channel.*;
import com.example.telecom.notification.dto.NotificationRequest;
import com.example.telecom.notification.dto.SendResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationChannelSendTest {

    private SmsNotificationChannel smsChannel;
    private EmailNotificationChannel emailChannel;
    private WeComNotificationChannel wecomChannel;

    @BeforeEach
    void setUp() {
        smsChannel = new SmsNotificationChannel();
        emailChannel = new EmailNotificationChannel();
        wecomChannel = new WeComNotificationChannel();
    }

    @Test
    void smsChannelShouldSendSuccessfully() {
        NotificationRequest request = createRequest("SMS");
        SendResult result = smsChannel.send(request);
        assertTrue(result.isSuccess());
        assertEquals("SMS", result.getChannel());
        assertNotNull(result.getMessageId());
        assertTrue(result.getMessageId().startsWith("SMS-"));
    }

    @Test
    void emailChannelShouldSendSuccessfully() {
        NotificationRequest request = createRequest("EMAIL");
        SendResult result = emailChannel.send(request);
        assertTrue(result.isSuccess());
        assertEquals("EMAIL", result.getChannel());
        assertTrue(result.getMessageId().startsWith("EMAIL-"));
    }

    @Test
    void wecomChannelShouldSendSuccessfully() {
        NotificationRequest request = createRequest("WECOM");
        SendResult result = wecomChannel.send(request);
        assertTrue(result.isSuccess());
        assertEquals("WECOM", result.getChannel());
        assertTrue(result.getMessageId().startsWith("WECOM-"));
    }

    @Test
    void allChannelsShouldProduceUniqueMessageIds() {
        NotificationRequest request = createRequest("ALL");
        String smsId = smsChannel.send(request).getMessageId();
        String emailId = emailChannel.send(request).getMessageId();
        String wecomId = wecomChannel.send(request).getMessageId();

        assertNotEquals(smsId, emailId);
        assertNotEquals(emailId, wecomId);
        assertNotEquals(smsId, wecomId);
    }

    @Test
    void channelsShouldAllImplementNotificationChannelInterface() {
        assertTrue(smsChannel instanceof NotificationChannel);
        assertTrue(emailChannel instanceof NotificationChannel);
        assertTrue(wecomChannel instanceof NotificationChannel);
    }

    private NotificationRequest createRequest(String channel) {
        NotificationRequest request = new NotificationRequest();
        request.setWorkOrderId("wo-test-1");
        request.setChannel(channel);
        request.setRecipient("test@telecom.local");
        request.setSubject("Test Subject");
        request.setBody("Test Body");
        return request;
    }
}
