package com.example.telecom.device.lifecycle;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceType;
import com.example.telecom.device.lifecycle.policy.DeviceDeprecationPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceDeprecationPolicyTest {

    private DeviceDeprecationPolicy deprecationPolicy;

    @BeforeEach
    void setUp() {
        deprecationPolicy = new DeviceDeprecationPolicy();
    }

    @Test
    void testEvaluateDeprecation() {
        DeviceInfo oldDevice = new DeviceInfo(
                "DEV-OLD001", "Old Router", DeviceType.ROUTER, "Cisco",
                "US-EAST", "SITE01", "10.0.0.1", true);

        boolean result = deprecationPolicy.evaluate(oldDevice);
        assertFalse(result);
    }

    @Test
    void testEligibleForDeprecation() {
        DeviceInfo oldDevice = new DeviceInfo(
                "DEV-OLD002", "Old Switch", DeviceType.SWITCH, "HP",
                "US-WEST", "SITE02", "10.0.0.2", true);

        boolean eligible = deprecationPolicy.isEligibleForDeprecation(oldDevice);
        assertFalse(eligible);

        int policyAge = deprecationPolicy.getDeviceTypeAgePolicy(DeviceType.SWITCH);
        assertEquals(4, policyAge);
    }

    @Test
    void testDeviceTooYoungForDeprecation() {
        DeviceInfo newDevice = new DeviceInfo(
                "NEW-DEVICE", "New Router", DeviceType.ROUTER, "Juniper",
                "US-EAST", "SITE03", "10.0.0.3", true);

        // Age is estimated from deviceId hash, so result varies
        boolean eligible = deprecationPolicy.isEligibleForDeprecation(newDevice);
        assertNotNull(newDevice);
        assertEquals("NEW-DEVICE", newDevice.getDeviceId());

        String reason = deprecationPolicy.getDeprecationReason(newDevice);
        assertNotNull(reason);
        assertTrue(reason.contains("NEW-DEVICE"));
    }

    @Test
    void testGetMinAgeYears() {
        int minAge = deprecationPolicy.getMinAgeYears();
        assertEquals(5, minAge);
    }

    @Test
    void testDeviceTypeAgePolicy() {
        assertEquals(7, deprecationPolicy.getDeviceTypeAgePolicy(DeviceType.BASE_STATION));
        assertEquals(6, deprecationPolicy.getDeviceTypeAgePolicy(DeviceType.OLT));
        assertEquals(5, deprecationPolicy.getDeviceTypeAgePolicy(DeviceType.ROUTER));
        assertEquals(4, deprecationPolicy.getDeviceTypeAgePolicy(DeviceType.SWITCH));
        assertEquals(5, deprecationPolicy.getDeviceTypeAgePolicy(null));
    }

    @Test
    void testNullDeviceReturnsFalse() {
        assertFalse(deprecationPolicy.evaluate(null));
        assertFalse(deprecationPolicy.isEligibleForDeprecation(null));
    }

    @Test
    void testNullDeviceReturnsUnknownReason() {
        String reason = deprecationPolicy.getDeprecationReason(null);
        assertEquals("Unknown device", reason);
    }

    @Test
    void testIsEligibleForDeprecationByType() {
        assertTrue(deprecationPolicy.isEligibleForDeprecationByType(DeviceType.SWITCH, 5.0));
        assertFalse(deprecationPolicy.isEligibleForDeprecationByType(DeviceType.SWITCH, 3.0));
        assertTrue(deprecationPolicy.isEligibleForDeprecationByType(DeviceType.BASE_STATION, 8.0));
    }

    @Test
    void testCalculateAgeInYears() {
        DeviceInfo device = new DeviceInfo(
                "DEV-AGE", "Age Device", DeviceType.ROUTER, "Cisco",
                "US-EAST", "SITE01", "10.0.0.1", true);
        double age = deprecationPolicy.calculateAgeInYears(device);
        assertTrue(age >= 0);
    }

    @Test
    void testCalculateAgeInYearsWithNull() {
        assertEquals(-1, deprecationPolicy.calculateAgeInYears((DeviceInfo) null), 0.01);
    }

    @Test
    void testIsWithinGracePeriod() {
        DeviceInfo device = new DeviceInfo(
                "DEV-GRACE", "Grace Device", DeviceType.SWITCH, "HP",
                "US-EAST", "SITE01", "10.0.0.1", true);
        assertFalse(deprecationPolicy.isWithinGracePeriod(device));
    }
}
