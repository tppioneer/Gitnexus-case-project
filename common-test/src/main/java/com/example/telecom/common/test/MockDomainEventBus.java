package com.example.telecom.common.test;

import com.example.telecom.common.alarm.AlarmEvent;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.common.workorder.WorkOrderEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock implementation of {@link DomainEventBus} for use in tests.
 *
 * <p>Published events are recorded in an in-memory list and can be inspected
 * or cleared between test cases.
 */
public class MockDomainEventBus implements DomainEventBus {

    private final List<Object> publishedEvents = new ArrayList<>();

    @Override
    public void publish(final DeviceMetricEvent event) {
        publishedEvents.add(event);
    }

    @Override
    public void publish(final AlarmEvent event) {
        publishedEvents.add(event);
    }

    @Override
    public void publish(final WorkOrderEvent event) {
        publishedEvents.add(event);
    }

    /**
     * Returns an unmodifiable view of all events published so far.
     *
     * @return list of published events in publication order
     */
    public List<Object> getPublishedEvents() {
        return List.copyOf(publishedEvents);
    }

    /**
     * Returns all published events of the given type.
     *
     * @param type the event class to filter by
     * @param <T>  the event type
     * @return list of matching events (never {@code null})
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getPublishedEventsOfType(final Class<T> type) {
        return publishedEvents.stream()
                .filter(type::isInstance)
                .map(e -> (T) e)
                .collect(Collectors.toList());
    }

    /**
     * Returns the total number of events published so far.
     *
     * @return event count
     */
    public int getPublishedCount() {
        return publishedEvents.size();
    }

    /**
     * Clears all published events.
     */
    public void clear() {
        publishedEvents.clear();
    }
}
