package com.example.telecom.collector.validator;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;
import com.example.telecom.common.exception.ValidationException;

public class DeviceMetricValidator {

    public void validate(DeviceMetric metric, DeviceInfo deviceInfo) {
        if (metric == null) {
            throw new ValidationException("metric", "Metric must not be null");
        }
        if (deviceInfo == null) {
            throw new ValidationException("deviceInfo", "Device info must not be null");
        }
        if (!deviceInfo.isActive()) {
            throw new ValidationException("deviceId", "Device is not active: " + deviceInfo.getDeviceId());
        }
        if (metric.getValue() < 0) {
            throw new ValidationException("value", "Metric value must not be negative for " + metric.getMetricType());
        }
        validateRange(metric);
    }

    private void validateRange(DeviceMetric metric) {
        MetricType type = metric.getMetricType();
        double value = metric.getValue();
        switch (type) {
            case CPU_USAGE, MEMORY_USAGE:
                if (value > 100.0) {
                    throw new ValidationException("value", type + " exceeds 100%: " + value);
                }
                break;
            case PACKET_LOSS:
                if (value > 100.0 || value < 0.0) {
                    throw new ValidationException("value", "Packet loss out of range: " + value);
                }
                break;
            case OPTICAL_POWER:
                if (value < -50.0 || value > 10.0) {
                    throw new ValidationException("value", "Optical power out of range: " + value + " dBm");
                }
                break;
            case TEMPERATURE:
                if (value < -40.0 || value > 85.0) {
                    throw new ValidationException("value", "Temperature out of range: " + value + "°C");
                }
                break;
        }
    }
}
