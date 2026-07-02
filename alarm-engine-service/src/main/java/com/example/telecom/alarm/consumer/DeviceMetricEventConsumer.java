package com.example.telecom.alarm.consumer;

import com.example.telecom.alarm.service.AlarmEvaluationService;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.event.MetricEventConsumer;

/**
 * Consumes DeviceMetricEvent from the event bus.
 * Implements the common-domain MetricEventConsumer interface.
 * Called by InMemoryDomainEventBus.publish(DeviceMetricEvent).
 * This is a key node in Case A flow tracing.
 */
public class DeviceMetricEventConsumer implements MetricEventConsumer {

    private final AlarmEvaluationService alarmEvaluationService;

    public DeviceMetricEventConsumer(AlarmEvaluationService alarmEvaluationService) {
        this.alarmEvaluationService = alarmEvaluationService;
    }

    @Override
    public void onMetric(DeviceMetricEvent event) {
        alarmEvaluationService.evaluate(event);
    }
}
