package com.example.telecom.alarm.event;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.event.DomainEventBus;

/**
 * Publishes alarm events via DomainEventBus.
 * MUST call DomainEventBus.publish(AlarmEvent) — not directly call consumer.
 */
public class AlarmEventPublisher {

    private final DomainEventBus domainEventBus;

    public AlarmEventPublisher(DomainEventBus domainEventBus) {
        this.domainEventBus = domainEventBus;
    }

    public void publish(AlarmEvent event) {
        domainEventBus.publish(event);
    }
}
