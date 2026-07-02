package com.example.telecom.common.event;

import com.example.telecom.common.device.DeviceMetricEvent;

/**
 * Consumer interface for device metric events.
 * Implemented by alarm-engine-service to consume metric events.
 */
public interface MetricEventConsumer {
    void onMetric(DeviceMetricEvent event);
}
