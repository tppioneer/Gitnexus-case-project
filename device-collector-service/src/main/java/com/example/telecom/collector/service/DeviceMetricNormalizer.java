package com.example.telecom.collector.service;

import com.example.telecom.common.device.DeviceMetric;

public class DeviceMetricNormalizer {

    public DeviceMetric normalize(DeviceMetric metric) {
        // Mark as normalized and round to 2 decimal places for consistency
        metric.setNormalized(true);
        metric.setValue(Math.round(metric.getValue() * 100.0) / 100.0);
        return metric;
    }
}
