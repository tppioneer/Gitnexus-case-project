package com.example.telecom.collector.event;

import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.event.DomainEventBus;

/**
 * Publishes device metric events via DomainEventBus.
 * MUST call DomainEventBus.publish(DeviceMetricEvent) — not directly call consumer.
 */
public class MetricEventPublisher {

    private final DomainEventBus domainEventBus;

    public MetricEventPublisher(DomainEventBus domainEventBus) {
        this.domainEventBus = domainEventBus;
    }

    public void publish(DeviceMetricEvent event) {
        domainEventBus.publish(event);
    }
}
