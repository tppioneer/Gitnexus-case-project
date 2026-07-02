package com.example.telecom.notification;

import com.example.telecom.notification.dto.NotificationResponse;
import com.example.telecom.notification.mapper.NotificationMapper;
import com.example.telecom.notification.template.NotificationRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    private final NotificationMapper mapper = new NotificationMapper();

    @Test
    void shouldMapRecordToResponse() {
        NotificationRecord record = new NotificationRecord("r1", "wo-1", "SMS",
                "13800000001", "Alert", "Device alert body", true, 1000L);
        NotificationResponse response = mapper.toResponse(record);

        assertEquals("r1", response.getRecordId());
        assertEquals("wo-1", response.getWorkOrderId());
        assertEquals("SMS", response.getChannel());
        assertEquals("Alert", response.getSubject());
        assertTrue(response.isSent());
        assertEquals(1000L, response.getSentTime());
    }

    @Test
    void shouldMapUnsentRecord() {
        NotificationRecord record = new NotificationRecord("r2", "wo-2", "EMAIL",
                "ops@telecom.local", "Update", "Status update", false, 2000L);
        NotificationResponse response = mapper.toResponse(record);

        assertFalse(response.isSent());
    }

    @Test
    void shouldMapDifferentChannels() {
        NotificationRecord sms = new NotificationRecord("r1", "wo-1", "SMS",
                "111", "S", "B", true, 1L);
        NotificationRecord email = new NotificationRecord("r2", "wo-1", "EMAIL",
                "a@b.com", "S", "B", true, 2L);
        NotificationRecord wecom = new NotificationRecord("r3", "wo-1", "WECOM",
                "wecom-id", "S", "B", true, 3L);

        assertEquals("SMS", mapper.toResponse(sms).getChannel());
        assertEquals("EMAIL", mapper.toResponse(email).getChannel());
        assertEquals("WECOM", mapper.toResponse(wecom).getChannel());
    }
}
