package com.example.telecom.common.event;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.workorder.WorkOrderEvent;

/**
 * Event bus interface for cross-service event delivery.
 * In production this would be backed by Kafka/RabbitMQ.
 * For benchmark, InMemoryDomainEventBus provides explicit Java method call tracing.
 */
public interface DomainEventBus {
    void publish(DeviceMetricEvent event);
    void publish(AlarmEvent event);
    void publish(WorkOrderEvent event);
}
