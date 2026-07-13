package com.example.telecom.dispatch.event;

import com.example.telecom.common.dispatch.DispatchOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class DispatchEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DispatchEventPublisher.class);

    public void publishAssigned(DispatchOrder order) {
        log.info("Dispatch assigned: orderId={}, workOrderId={}, assigneeId={}",
                order.getOrderId(), order.getWorkOrderId(), order.getAssigneeId());
    }

    public void publishCancelled(DispatchOrder order) {
        log.info("Dispatch cancelled: orderId={}, workOrderId={}",
                order.getOrderId(), order.getWorkOrderId());
    }

    public void publishReassigned(DispatchOrder order, String previousAssignee) {
        log.info("Dispatch reassigned: orderId={}, workOrderId={}, previousAssigneeId={}, newAssigneeId={}",
                order.getOrderId(), order.getWorkOrderId(), previousAssignee, order.getAssigneeId());
    }

    public void publishCompleted(DispatchOrder order) {
        log.info("Dispatch completed: orderId={}, workOrderId={}, assigneeId={}",
                order.getOrderId(), order.getWorkOrderId(), order.getAssigneeId());
    }

    public void publishFailed(DispatchOrder order, String reason) {
        log.warn("Dispatch failed: orderId={}, workOrderId={}, reason={}",
                order.getOrderId(), order.getWorkOrderId(), reason);
    }

    public void publishPending(DispatchOrder order) {
        log.info("Dispatch pending: orderId={}, workOrderId={}, priority={}",
                order.getOrderId(), order.getWorkOrderId(), order.getPriority());
    }

    public Map<String, Object> buildEventPayload(DispatchOrder order, String eventType) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", eventType);
        payload.put("orderId", order.getOrderId());
        payload.put("workOrderId", order.getWorkOrderId());
        payload.put("assigneeId", order.getAssigneeId());
        payload.put("priority", order.getPriority());
        payload.put("status", order.getStatus());
        payload.put("timestamp", LocalDateTime.now().toString());
        return payload;
    }
}
