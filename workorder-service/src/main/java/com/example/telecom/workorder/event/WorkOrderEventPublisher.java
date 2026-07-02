package com.example.telecom.workorder.event;

import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderEvent;
import com.example.telecom.common.workorder.WorkOrderStatus;

import java.util.UUID;

/**
 * Publishes work order events via DomainEventBus.
 * MUST call DomainEventBus.publish(WorkOrderEvent) — not directly call consumer.
 */
public class WorkOrderEventPublisher {

    private final DomainEventBus domainEventBus;

    public WorkOrderEventPublisher(DomainEventBus domainEventBus) {
        this.domainEventBus = domainEventBus;
    }

    public void publish(WorkOrder workOrder, WorkOrderStatus fromStatus, WorkOrderStatus toStatus) {
        WorkOrderEvent event = new WorkOrderEvent(
                UUID.randomUUID().toString(),
                workOrder.getWorkOrderId(),
                fromStatus,
                toStatus,
                workOrder.getAssignee(),
                workOrder.getMaintenanceRegionCode(),
                System.currentTimeMillis()
        );
        domainEventBus.publish(event);
    }
}
