package com.example.telecom.common;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DeviceInfo.regionCode — Case C source field.
 */
class DeviceInfoRegionCodeTest {

    @Test
    void shouldSetAndGetRegionCode() {
        DeviceInfo deviceInfo = new DeviceInfo("dev-1", "BS-01", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-1", "10.0.0.1", true);
        assertEquals("EAST", deviceInfo.getRegionCode());
    }

    @Test
    void shouldAllowNullRegionCode() {
        DeviceInfo deviceInfo = new DeviceInfo();
        assertNull(deviceInfo.getRegionCode());
    }
}
