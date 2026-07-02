package com.example.telecom.notification.consumer;

import com.example.telecom.notification.service.NotificationService;
import com.example.telecom.common.workorder.WorkOrderEvent;

/**
 * Consumes WorkOrderEvent from the event bus.
 * Implements the common-domain WorkOrderEventConsumer interface.
 * Called by InMemoryDomainEventBus.publish(WorkOrderEvent).
 */
public class WorkOrderEventConsumer implements com.example.telecom.common.event.WorkOrderEventConsumer {

    private final NotificationService notificationService;

    public WorkOrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onWorkOrderChanged(WorkOrderEvent event) {
        notificationService.process(event);
    }
}
