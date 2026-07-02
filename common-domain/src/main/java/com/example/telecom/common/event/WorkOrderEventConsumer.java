package com.example.telecom.common.event;

import com.example.telecom.common.workorder.WorkOrderEvent;

/**
 * Consumer interface for work order events.
 * Implemented by notification-service to consume work order events.
 */
public interface WorkOrderEventConsumer {
    void onWorkOrderChanged(WorkOrderEvent event);
}
