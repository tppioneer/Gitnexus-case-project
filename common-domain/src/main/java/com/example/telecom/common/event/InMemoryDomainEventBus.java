package com.example.telecom.common.event;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.workorder.WorkOrderEvent;

/**
 * In-memory implementation of DomainEventBus.
 * EXPLICITLY calls consumer methods for static code analysis visibility.
 * This is the critical event bridge that enables Case A flow tracing.
 */
public class InMemoryDomainEventBus implements DomainEventBus {

    private final MetricEventConsumer metricConsumer;
    private final AlarmEventConsumer alarmConsumer;
    private final WorkOrderEventConsumer workOrderConsumer;

    public InMemoryDomainEventBus(MetricEventConsumer metricConsumer,
                                   AlarmEventConsumer alarmConsumer,
                                   WorkOrderEventConsumer workOrderConsumer) {
        this.metricConsumer = metricConsumer;
        this.alarmConsumer = alarmConsumer;
        this.workOrderConsumer = workOrderConsumer;
    }

    @Override
    public void publish(DeviceMetricEvent event) {
        metricConsumer.onMetric(event);
    }

    @Override
    public void publish(AlarmEvent event) {
        alarmConsumer.onAlarmCreated(event);
    }

    @Override
    public void publish(WorkOrderEvent event) {
        workOrderConsumer.onWorkOrderChanged(event);
    }
}
