package com.example.telecom.collector;

import com.example.telecom.collector.adapter.HuaweiVendorAdapter;
import com.example.telecom.common.device.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HuaweiVendorAdapterTest {

    private HuaweiVendorAdapter adapter;
    private DeviceInfo deviceInfo;

    @BeforeEach
    void setUp() {
        adapter = new HuaweiVendorAdapter();
        deviceInfo = new DeviceInfo("dev-hw-1", "BS-East", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S1", "10.0.0.1", true);
    }

    @Test
    void shouldNormalizeCpuMetric() {
        RawDeviceMetric raw = new RawDeviceMetric("dev-hw-1", "Huawei", "cpu_usage_ratio",
                8500.0, "ratio", System.currentTimeMillis());
        DeviceMetric metric = adapter.normalizeRawMetric(raw, deviceInfo);

        assertEquals(MetricType.CPU_USAGE, metric.getMetricType());
        assertEquals(85.0, metric.getValue(), 0.01);
        assertEquals("%", metric.getUnit());
    }

    @Test
    void shouldNormalizeMemoryMetric() {
        // Huawei mem_usage_ratio is not scaled (unlike CPU which divides by 100)
        RawDeviceMetric raw = new RawDeviceMetric("dev-hw-1", "Huawei", "mem_usage_ratio",
                72.0, "%", System.currentTimeMillis());
        DeviceMetric metric = adapter.normalizeRawMetric(raw, deviceInfo);

        assertEquals(MetricType.MEMORY_USAGE, metric.getMetricType());
        assertEquals(72.0, metric.getValue(), 0.01);
    }

    @Test
    void shouldNormalizeOpticalPowerMetric() {
        RawDeviceMetric raw = new RawDeviceMetric("dev-hw-1", "Huawei", "optical_rx_power",
                -2850.0, "0.01dBm", System.currentTimeMillis());
        DeviceMetric metric = adapter.normalizeRawMetric(raw, deviceInfo);

        assertEquals(MetricType.OPTICAL_POWER, metric.getMetricType());
        assertEquals(-28.5, metric.getValue(), 0.01);
        assertEquals("dBm", metric.getUnit());
    }

    @Test
    void shouldNormalizePacketLossMetric() {
        RawDeviceMetric raw = new RawDeviceMetric("dev-hw-1", "Huawei", "port_packet_loss",
                3.5, "%", System.currentTimeMillis());
        DeviceMetric metric = adapter.normalizeRawMetric(raw, deviceInfo);

        assertEquals(MetricType.PACKET_LOSS, metric.getMetricType());
        assertEquals(3.5, metric.getValue(), 0.01);
    }

    @Test
    void shouldNormalizeTemperatureMetric() {
        RawDeviceMetric raw = new RawDeviceMetric("dev-hw-1", "Huawei", "board_temperature",
                45.0, "°C", System.currentTimeMillis());
        DeviceMetric metric = adapter.normalizeRawMetric(raw, deviceInfo);

        assertEquals(MetricType.TEMPERATURE, metric.getMetricType());
        assertEquals(45.0, metric.getValue(), 0.01);
        assertEquals("°C", metric.getUnit());
    }

    @Test
    void shouldThrowForUnknownMetric() {
        RawDeviceMetric raw = new RawDeviceMetric("dev-hw-1", "Huawei", "unknown_metric",
                100.0, "x", System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, () -> adapter.normalizeRawMetric(raw, deviceInfo));
    }
}
