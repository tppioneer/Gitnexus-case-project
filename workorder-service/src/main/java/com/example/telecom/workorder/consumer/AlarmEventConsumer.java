package com.example.telecom.workorder.consumer;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.workorder.service.AutoWorkOrderService;

/**
 * Consumes AlarmEvent from the event bus.
 * Implements the common-domain AlarmEventConsumer interface.
 * Called by InMemoryDomainEventBus.publish(AlarmEvent).
 * This is a key node in Case A flow tracing.
 */
public class AlarmEventConsumer implements com.example.telecom.common.event.AlarmEventConsumer {

    private final AutoWorkOrderService autoWorkOrderService;

    public AlarmEventConsumer(AutoWorkOrderService autoWorkOrderService) {
        this.autoWorkOrderService = autoWorkOrderService;
    }

    @Override
    public void onAlarmCreated(AlarmEvent event) {
        autoWorkOrderService.createForAlarm(event);
    }
}
