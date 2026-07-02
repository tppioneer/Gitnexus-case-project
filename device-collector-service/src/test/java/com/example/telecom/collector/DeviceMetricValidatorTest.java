package com.example.telecom.collector;

import com.example.telecom.collector.validator.DeviceMetricValidator;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.DeviceType;
import com.example.telecom.common.device.MetricType;
import com.example.telecom.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMetricValidatorTest {

    private DeviceMetricValidator validator;
    private DeviceInfo activeDevice;

    @BeforeEach
    void setUp() {
        validator = new DeviceMetricValidator();
        activeDevice = new DeviceInfo("dev-1", "BS-01", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-1", "10.0.0.1", true);
    }

    @Test
    void shouldValidateNormalMetric() {
        DeviceMetric metric = new DeviceMetric("m1", "dev-1", MetricType.CPU_USAGE, 50.0,
                "%", Instant.now(), "Huawei");
        assertDoesNotThrow(() -> validator.validate(metric, activeDevice));
    }

    @Test
    void shouldRejectInactiveDevice() {
        DeviceInfo inactive = new DeviceInfo("dev-2", "BS-02", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-2", "10.0.0.2", false);
        DeviceMetric metric = new DeviceMetric("m2", "dev-2", MetricType.CPU_USAGE, 50.0,
                "%", Instant.now(), "Huawei");
        assertThrows(ValidationException.class, () -> validator.validate(metric, inactive));
    }

    @Test
    void shouldRejectNegativeValue() {
        DeviceMetric metric = new DeviceMetric("m3", "dev-1", MetricType.TEMPERATURE, -100.0,
                "°C", Instant.now(), "Huawei");
        assertThrows(ValidationException.class, () -> validator.validate(metric, activeDevice));
    }
}
