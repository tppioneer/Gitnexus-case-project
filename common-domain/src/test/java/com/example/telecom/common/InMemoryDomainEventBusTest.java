package com.example.telecom.common;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.event.*;
import com.example.telecom.common.workorder.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryDomainEventBusTest {

    private InMemoryDomainEventBus bus;
    private AtomicInteger metricCount;
    private AtomicInteger alarmCount;
    private AtomicInteger workOrderCount;

    @BeforeEach
    void setUp() {
        metricCount = new AtomicInteger(0);
        alarmCount = new AtomicInteger(0);
        workOrderCount = new AtomicInteger(0);

        bus = new InMemoryDomainEventBus(
                event -> metricCount.incrementAndGet(),
                event -> alarmCount.incrementAndGet(),
                event -> workOrderCount.incrementAndGet()
        );
    }

    @Test
    void shouldRouteDeviceMetricEventToMetricConsumer() {
        DeviceMetricEvent event = new DeviceMetricEvent("e1", "d1", "m1", "CPU_USAGE",
                90.0, "%", System.currentTimeMillis(), "EAST");
        bus.publish(event);
        assertEquals(1, metricCount.get());
        assertEquals(0, alarmCount.get());
        assertEquals(0, workOrderCount.get());
    }

    @Test
    void shouldRouteAlarmEventToAlarmConsumer() {
        AlarmEvent event = new AlarmEvent("e1", "a1", "d1", Severity.CRITICAL,
                AlarmStatus.OPEN, "EAST", System.currentTimeMillis());
        bus.publish(event);
        assertEquals(0, metricCount.get());
        assertEquals(1, alarmCount.get());
        assertEquals(0, workOrderCount.get());
    }

    @Test
    void shouldRouteWorkOrderEventToWorkOrderConsumer() {
        WorkOrderEvent event = new WorkOrderEvent("e1", "w1", WorkOrderStatus.CREATED,
                WorkOrderStatus.ASSIGNED, "op-1", "EAST", System.currentTimeMillis());
        bus.publish(event);
        assertEquals(0, metricCount.get());
        assertEquals(0, alarmCount.get());
        assertEquals(1, workOrderCount.get());
    }
}
