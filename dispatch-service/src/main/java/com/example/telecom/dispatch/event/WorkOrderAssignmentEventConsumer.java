package com.example.telecom.dispatch.event;

import com.example.telecom.common.dispatch.DispatchOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderAssignmentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderAssignmentEventConsumer.class);

    public void onWorkOrderAssigned(String orderId, String workOrderId, String assigneeId) {
        log.info("Work order assigned event consumed: orderId={}, workOrderId={}, assigneeId={}",
                orderId, workOrderId, assigneeId);
    }

    public void onWorkOrderCancelled(String orderId, String workOrderId) {
        log.info("Work order cancelled event consumed: orderId={}, workOrderId={}",
                orderId, workOrderId);
    }

    public void onWorkOrderReassigned(String orderId, String workOrderId,
                                       String previousAssignee, String newAssignee) {
        log.info("Work order reassigned event consumed: orderId={}, workOrderId={}, previousAssigneeId={}, newAssigneeId={}",
                orderId, workOrderId, previousAssignee, newAssignee);
    }

    public void onWorkOrderCompleted(String orderId, String workOrderId, String assigneeId) {
        log.info("Work order completed event consumed: orderId={}, workOrderId={}, assigneeId={}",
                orderId, workOrderId, assigneeId);
    }

    public void onDispatchOrderCreated(DispatchOrder order) {
        log.info("Dispatch order created event consumed: orderId={}, workOrderId={}, status={}",
                order.getOrderId(), order.getWorkOrderId(), order.getStatus());
    }

    public void onDispatchOrderUpdated(DispatchOrder order, String previousStatus) {
        log.info("Dispatch order updated: orderId={}, status={} -> {}",
                order.getOrderId(), previousStatus, order.getStatus());
    }

    public boolean handleEvent(String eventType, String orderId, String workOrderId) {
        log.debug("Handling event type={} for orderId={}", eventType, orderId);
        if (eventType == null || orderId == null) {
            log.warn("Invalid event data: eventType={}, orderId={}", eventType, orderId);
            return false;
        }
        switch (eventType) {
            case "ASSIGNED":
                onWorkOrderAssigned(orderId, workOrderId, null);
                return true;
            case "CANCELLED":
                onWorkOrderCancelled(orderId, workOrderId);
                return true;
            case "COMPLETED":
                onWorkOrderCompleted(orderId, workOrderId, null);
                return true;
            default:
                log.warn("Unknown event type: {}", eventType);
                return false;
        }
    }
}
