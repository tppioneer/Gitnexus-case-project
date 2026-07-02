package com.example.telecom.common;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.alarm.AlarmStatus;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.event.*;
import com.example.telecom.common.workorder.WorkOrderEvent;
import com.example.telecom.common.workorder.WorkOrderStatus;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates that InMemoryDomainEventBus correctly routes each event type
 * to the appropriate consumer. This is critical for Case A flow tracing.
 */
class EventBusRoutingTest {

    @Test
    void shouldRouteMetricEventToMetricConsumer() {
        AtomicInteger count = new AtomicInteger(0);
        InMemoryDomainEventBus bus = new InMemoryDomainEventBus(
                event -> count.incrementAndGet(),
                event -> fail("Should not route to alarm consumer"),
                event -> fail("Should not route to work order consumer")
        );

        DeviceMetricEvent event = new DeviceMetricEvent("e1", "d1", "m1", "CPU_USAGE",
                90.0, "%", System.currentTimeMillis(), "EAST");
        bus.publish(event);
        assertEquals(1, count.get());
    }

    @Test
    void shouldRouteAlarmEventToAlarmConsumer() {
        AtomicInteger count = new AtomicInteger(0);
        InMemoryDomainEventBus bus = new InMemoryDomainEventBus(
                event -> fail("Should not route to metric consumer"),
                event -> count.incrementAndGet(),
                event -> fail("Should not route to work order consumer")
        );

        AlarmEvent event = new AlarmEvent("e1", "a1", "d1", Severity.CRITICAL,
                AlarmStatus.OPEN, "EAST", System.currentTimeMillis());
        bus.publish(event);
        assertEquals(1, count.get());
    }

    @Test
    void shouldRouteWorkOrderEventToWorkOrderConsumer() {
        AtomicInteger count = new AtomicInteger(0);
        InMemoryDomainEventBus bus = new InMemoryDomainEventBus(
                event -> fail("Should not route to metric consumer"),
                event -> fail("Should not route to alarm consumer"),
                event -> count.incrementAndGet()
        );

        WorkOrderEvent event = new WorkOrderEvent("e1", "w1", WorkOrderStatus.CREATED,
                WorkOrderStatus.ASSIGNED, "op-1", "EAST", System.currentTimeMillis());
        bus.publish(event);
        assertEquals(1, count.get());
    }

    @Test
    void shouldRouteMultipleEventTypesIndependently() {
        AtomicInteger metricCount = new AtomicInteger(0);
        AtomicInteger alarmCount = new AtomicInteger(0);
        AtomicInteger woCount = new AtomicInteger(0);

        InMemoryDomainEventBus bus = new InMemoryDomainEventBus(
                event -> metricCount.incrementAndGet(),
                event -> alarmCount.incrementAndGet(),
                event -> woCount.incrementAndGet()
        );

        bus.publish(new DeviceMetricEvent("e1", "d1", "m1", "CPU_USAGE",
                90.0, "%", 1000L, "EAST"));
        bus.publish(new DeviceMetricEvent("e2", "d2", "m2", "MEMORY_USAGE",
                85.0, "%", 2000L, "WEST"));
        bus.publish(new AlarmEvent("e3", "a1", "d1", Severity.MAJOR,
                AlarmStatus.OPEN, "EAST", 3000L));
        bus.publish(new WorkOrderEvent("e4", "w1", WorkOrderStatus.CREATED,
                WorkOrderStatus.ASSIGNED, "op-1", "EAST", 4000L));

        assertEquals(2, metricCount.get());
        assertEquals(1, alarmCount.get());
        assertEquals(1, woCount.get());
    }

    @Test
    void domainEventBusInterfaceShouldHaveAllThreePublishMethods() {
        // Verify the interface exists and has the right methods
        assertTrue(DomainEventBus.class.isInterface());
        assertEquals(3, DomainEventBus.class.getDeclaredMethods().length);
    }
}
