package com.example.telecom.workorder.event;

import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.common.workorder.SlaBreachEvent;
import com.example.telecom.common.workorder.SlaPolicy;
import com.example.telecom.common.workorder.WorkOrder;

import java.util.UUID;

public class SlaBreachEventPublisher {

    private final DomainEventBus domainEventBus;

    public SlaBreachEventPublisher(DomainEventBus domainEventBus) {
        this.domainEventBus = domainEventBus;
    }

    public void publish(SlaBreachEvent event) {
        // Publish via event bus — can be consumed by notification or escalation services
        domainEventBus.publish(convertToWorkOrderEvent(event));
    }

    public SlaBreachEvent createBreachEvent(WorkOrder workOrder, SlaPolicy policy, String breachType) {
        long elapsed = System.currentTimeMillis() - workOrder.getCreatedTime();
        long threshold = "RESPONSE".equals(breachType) ? policy.getResponseTimeMs() : policy.getResolutionTimeMs();
        return new SlaBreachEvent(
                UUID.randomUUID().toString(),
                workOrder.getWorkOrderId(),
                policy.getSlaPolicyId(),
                breachType,
                elapsed,
                threshold
        );
    }

    private com.example.telecom.common.workorder.WorkOrderEvent convertToWorkOrderEvent(SlaBreachEvent breach) {
        return new com.example.telecom.common.workorder.WorkOrderEvent(
                "sla-" + breach.getEventId(),
                breach.getWorkOrderId(),
                null,
                com.example.telecom.common.workorder.WorkOrderStatus.ESCALATED,
                "system",
                null,
                breach.getEventTimestamp()
        );
    }
}
