package com.example.telecom.notification;

import com.example.telecom.common.workorder.WorkOrderEvent;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.notification.service.NotificationTemplateService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTemplateServiceTest {

    @Test
    void shouldRenderSubjectWithStatusTransition() {
        NotificationTemplateService service = new NotificationTemplateService();
        WorkOrderEvent event = new WorkOrderEvent("e1", "wo-1", WorkOrderStatus.CREATED,
                WorkOrderStatus.ASSIGNED, "op-1", "EAST", System.currentTimeMillis());

        String subject = service.renderSubject(event);
        assertTrue(subject.contains("wo-1"));
        assertTrue(subject.contains("CREATED"));
        assertTrue(subject.contains("ASSIGNED"));
    }

    @Test
    void shouldRenderBodyWithRegionCode() {
        NotificationTemplateService service = new NotificationTemplateService();
        WorkOrderEvent event = new WorkOrderEvent("e2", "wo-2", WorkOrderStatus.PROCESSING,
                WorkOrderStatus.RESOLVED, "op-2", "SOUTH", System.currentTimeMillis());

        String body = service.renderBody(event);
        assertTrue(body.contains("SOUTH"));
        assertTrue(body.contains("wo-2"));
    }
}
