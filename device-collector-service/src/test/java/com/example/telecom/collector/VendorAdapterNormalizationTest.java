package com.example.telecom.collector;

import com.example.telecom.collector.adapter.*;
import com.example.telecom.common.device.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests normalization behavior across all vendor adapters.
 */
class VendorAdapterNormalizationTest {

    private HuaweiVendorAdapter huaweiAdapter;
    private ZteVendorAdapter zteAdapter;
    private FiberHomeVendorAdapter fiberHomeAdapter;
    private GenericSnmpVendorAdapter genericAdapter;
    private DeviceInfo deviceInfo;

    @BeforeEach
    void setUp() {
        huaweiAdapter = new HuaweiVendorAdapter();
        zteAdapter = new ZteVendorAdapter();
        fiberHomeAdapter = new FiberHomeVendorAdapter();
        genericAdapter = new GenericSnmpVendorAdapter();
        deviceInfo = new DeviceInfo("d1", "Test", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S1", "10.0.0.1", true);
    }

    @Test
    void allAdaptersShouldNormalizeCpuMetric() {
        RawDeviceMetric hwCpu = new RawDeviceMetric("d1", "Huawei", "cpu_usage_ratio",
                8500.0, "ratio", System.currentTimeMillis());
        RawDeviceMetric zteCpu = new RawDeviceMetric("d2", "ZTE", "cpu_load",
                75.0, "%", System.currentTimeMillis());
        RawDeviceMetric fhCpu = new RawDeviceMetric("d3", "FiberHome", "fh_cpu",
                80.0, "%", System.currentTimeMillis());
        RawDeviceMetric snmpCpu = new RawDeviceMetric("d4", "Generic", "snmp_cpu",
                60.0, "%", System.currentTimeMillis());

        assertNotNull(huaweiAdapter.normalizeRawMetric(hwCpu, deviceInfo));
        assertNotNull(zteAdapter.normalizeRawMetric(zteCpu, deviceInfo));
        assertNotNull(fiberHomeAdapter.normalizeRawMetric(fhCpu, deviceInfo));
        assertNotNull(genericAdapter.normalizeRawMetric(snmpCpu, deviceInfo));
    }

    @Test
    void allAdaptersShouldNormalizeTemperatureMetric() {
        RawDeviceMetric hwTemp = new RawDeviceMetric("d1", "Huawei", "board_temperature",
                45.0, "°C", System.currentTimeMillis());
        RawDeviceMetric zteTemp = new RawDeviceMetric("d2", "ZTE", "env_temp",
                50.0, "°C", System.currentTimeMillis());
        RawDeviceMetric fhTemp = new RawDeviceMetric("d3", "FiberHome", "fh_temperature",
                350.0, "0.1°C", System.currentTimeMillis());
        RawDeviceMetric snmpTemp = new RawDeviceMetric("d4", "Generic", "snmp_temp",
                55.0, "°C", System.currentTimeMillis());

        assertNotNull(huaweiAdapter.normalizeRawMetric(hwTemp, deviceInfo));
        assertNotNull(zteAdapter.normalizeRawMetric(zteTemp, deviceInfo));

        DeviceMetric fhResult = fiberHomeAdapter.normalizeRawMetric(fhTemp, deviceInfo);
        assertEquals(35.0, fhResult.getValue(), 0.01); // FiberHome divides by 10

        assertNotNull(genericAdapter.normalizeRawMetric(snmpTemp, deviceInfo));
    }

    @Test
    void allAdaptersShouldProduceUniqueMetricIds() {
        RawDeviceMetric raw = new RawDeviceMetric("d1", "Huawei", "cpu_usage_ratio",
                50.0, "ratio", System.currentTimeMillis());

        DeviceMetric m1 = huaweiAdapter.normalizeRawMetric(raw, deviceInfo);
        DeviceMetric m2 = huaweiAdapter.normalizeRawMetric(raw, deviceInfo);

        assertNotEquals(m1.getMetricId(), m2.getMetricId());
    }

    @Test
    void adaptersShouldSetCorrectVendorTag() {
        RawDeviceMetric raw = new RawDeviceMetric("d1", "Huawei", "cpu_usage_ratio",
                50.0, "ratio", System.currentTimeMillis());

        assertEquals("Huawei", huaweiAdapter.normalizeRawMetric(raw, deviceInfo).getVendor());
        assertEquals("ZTE", zteAdapter.normalizeRawMetric(
                new RawDeviceMetric("d1", "ZTE", "cpu_load", 50.0, "%", System.currentTimeMillis()),
                deviceInfo).getVendor());
        assertEquals("FiberHome", fiberHomeAdapter.normalizeRawMetric(
                new RawDeviceMetric("d1", "FiberHome", "fh_cpu", 50.0, "%", System.currentTimeMillis()),
                deviceInfo).getVendor());
        assertEquals("Generic", genericAdapter.normalizeRawMetric(
                new RawDeviceMetric("d1", "Generic", "snmp_cpu", 50.0, "%", System.currentTimeMillis()),
                deviceInfo).getVendor());
    }
}
