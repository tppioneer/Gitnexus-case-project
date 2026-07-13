package com.example.telecom.collector;

import com.example.telecom.collector.adapter.*;
import com.example.telecom.collector.dto.NormalizedAlarm;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VendorAlarmAdapterRegistryTest {

    private VendorAlarmAdapterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new VendorAlarmAdapterRegistry(List.of(
                new HuaweiVendorAlarmAdapter(),
                new ZteVendorAlarmAdapter(),
                new FiberHomeVendorAlarmAdapter()
        ));
    }

    @Test
    void shouldResolveHuaweiAdapter() {
        VendorAlarmAdapter adapter = registry.resolve("Huawei");
        assertNotNull(adapter);
        assertTrue(adapter instanceof HuaweiVendorAlarmAdapter);
        assertEquals("Huawei", adapter.getVendorType());
    }

    @Test
    void shouldResolveZteAdapter() {
        VendorAlarmAdapter adapter = registry.resolve("ZTE");
        assertNotNull(adapter);
        assertTrue(adapter instanceof ZteVendorAlarmAdapter);
    }

    @Test
    void shouldResolveFiberHomeAdapter() {
        VendorAlarmAdapter adapter = registry.resolve("FiberHome");
        assertNotNull(adapter);
        assertTrue(adapter instanceof FiberHomeVendorAlarmAdapter);
    }

    @Test
    void shouldThrowForUnknownVendor() {
        assertNull(registry.resolve("UnknownVendor"));
    }

    @Test
    void shouldNormalizeHuaweiAlarm() {
        String rawAlarm = "HUAWEI::ALARM::DEV-HW-001::POWER_FAILURE::CRITICAL::1700000000::Power supply unit A failed";
        NormalizedAlarm alarm = registry.normalize("Huawei", rawAlarm);

        assertNotNull(alarm);
        assertEquals("DEV-HW-001", alarm.getDeviceId());
        assertEquals("POWER_FAILURE", alarm.getAlarmType());
        assertEquals(Severity.CRITICAL, alarm.getSeverity());
        assertEquals("Huawei", alarm.getVendorType());
    }

    @Test
    void shouldNormalizeZteAlarm() {
        String rawAlarm = "ZTE:ALARM:DEV-ZTE-001:ALM-001:FAN_FAILURE:MAJOR:1700000000:Fan module 3 failure detected";
        NormalizedAlarm alarm = registry.normalize("ZTE", rawAlarm);

        assertNotNull(alarm);
        assertEquals("DEV-ZTE-001", alarm.getDeviceId());
        assertEquals("FAN_FAILURE", alarm.getAlarmType());
        assertEquals(Severity.MAJOR, alarm.getSeverity());
    }

    @Test
    void shouldNormalizeFiberHomeAlarm() {
        String rawAlarm = "FH-ALERT:DEV-FH-001|1024|2|1700000000|Optical module temperature high";
        NormalizedAlarm alarm = registry.normalize("FiberHome", rawAlarm);

        assertNotNull(alarm);
        assertEquals("DEV-FH-001", alarm.getDeviceId());
        assertEquals("FH_ALARM_1024", alarm.getAlarmType());
        assertEquals(Severity.MAJOR, alarm.getSeverity());
    }

    @Test
    void shouldReturnAllAdapters() {
        List<VendorAlarmAdapter> adapters = registry.getAllAdapters();
        assertEquals(3, adapters.size());
    }

    @Test
    void shouldRegisterNewAdapter() {
        VendorAlarmAdapter mockAdapter = new VendorAlarmAdapter() {
            @Override
            public NormalizedAlarm normalize(String rawAlarm) {
                return new NormalizedAlarm("src-1", "TEST", Severity.INFO, "dev-1",
                        System.currentTimeMillis(), "test", "TestVendor", rawAlarm);
            }

            @Override
            public String getVendorType() {
                return "TestVendor";
            }
        };

        registry.register(mockAdapter);
        assertTrue(registry.containsVendor("TestVendor"));
        assertEquals(4, registry.size());
    }
}
