package com.example.telecom.collector;

import com.example.telecom.collector.service.DeviceMetricNormalizer;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMetricNormalizerTest {

    private DeviceMetricNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new DeviceMetricNormalizer();
    }

    @Test
    void shouldMarkMetricAsNormalized() {
        DeviceMetric metric = new DeviceMetric("m1", "d1", MetricType.CPU_USAGE, 85.567,
                "%", Instant.now(), "Huawei");
        assertFalse(metric.isNormalized());
        normalizer.normalize(metric);
        assertTrue(metric.isNormalized());
    }

    @Test
    void shouldRoundValueToTwoDecimals() {
        DeviceMetric metric = new DeviceMetric("m2", "d2", MetricType.MEMORY_USAGE, 72.56789,
                "%", Instant.now(), "ZTE");
        DeviceMetric result = normalizer.normalize(metric);
        assertEquals(72.57, result.getValue(), 0.001);
    }

    @Test
    void shouldPreserveExactValues() {
        DeviceMetric metric = new DeviceMetric("m3", "d3", MetricType.OPTICAL_POWER, -20.0,
                "dBm", Instant.now(), "FiberHome");
        DeviceMetric result = normalizer.normalize(metric);
        assertEquals(-20.0, result.getValue(), 0.001);
    }

    @Test
    void shouldHandleZeroValue() {
        DeviceMetric metric = new DeviceMetric("m4", "d4", MetricType.PACKET_LOSS, 0.0,
                "%", Instant.now(), "Generic");
        DeviceMetric result = normalizer.normalize(metric);
        assertEquals(0.0, result.getValue(), 0.001);
        assertTrue(result.isNormalized());
    }

    @Test
    void shouldHandleLargeValue() {
        DeviceMetric metric = new DeviceMetric("m5", "d5", MetricType.TEMPERATURE, 95.123456,
                "C", Instant.now(), "Huawei");
        DeviceMetric result = normalizer.normalize(metric);
        assertEquals(95.12, result.getValue(), 0.001);
    }
}
