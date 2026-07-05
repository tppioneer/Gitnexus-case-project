package com.example.telecom.common;

import com.example.telecom.common.alarm.*;
import com.example.telecom.common.device.*;
import com.example.telecom.common.region.Region;
import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies JSON serialization round-trip for all domain objects.
 * Important for Case C: ensures regionCode/maintenanceRegionCode fields are serialized correctly.
 */
class SerializationRoundTripTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeDeviceInfo() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo("d1", "BS-East", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S1", "10.0.0.1", true);
        String json = mapper.writeValueAsString(deviceInfo);
        assertTrue(json.contains("maintenanceRegionCode"));
        assertTrue(json.contains("EAST"));

        DeviceInfo deserialized = mapper.readValue(json, DeviceInfo.class);
        assertEquals("EAST", deserialized.getMaintenanceRegionCode());
    }

    @Test
    void shouldSerializeDeviceMetricEvent() throws Exception {
        DeviceMetricEvent event = new DeviceMetricEvent("e1", "d1", "m1", "CPU_USAGE",
                90.0, "%", 1000L, "EAST");
        String json = mapper.writeValueAsString(event);
        assertTrue(json.contains("deviceRegionCode"));
        assertTrue(json.contains("EAST"));
    }

    @Test
    void shouldSerializeAlarmRecord() throws Exception {
        AlarmRecord alarm = new AlarmRecord("a1", "d1", "m1", "CPU_USAGE",
                Severity.CRITICAL, AlarmStatus.OPEN, "desc", "SOUTH", 2000L);
        String json = mapper.writeValueAsString(alarm);
        assertTrue(json.contains("alarmRegionCode"));
        assertTrue(json.contains("SOUTH"));
    }

    @Test
    void shouldSerializeAlarmEvent() throws Exception {
        AlarmEvent event = new AlarmEvent("e1", "a1", "d1", Severity.MAJOR,
                AlarmStatus.OPEN, "WEST", 3000L);
        String json = mapper.writeValueAsString(event);
        assertTrue(json.contains("deviceRegionCode"));
        assertTrue(json.contains("WEST"));
    }

    @Test
    void shouldSerializeWorkOrder() throws Exception {
        WorkOrder wo = new WorkOrder("w1", "a1", "d1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "title", "desc", "NORTH", 4000L);
        String json = mapper.writeValueAsString(wo);
        assertTrue(json.contains("maintenanceRegionCode"));
        assertTrue(json.contains("NORTH"));
    }

    @Test
    void shouldSerializeRegionDistraction() throws Exception {
        Region region = new Region("r1", "East Region", "EAST", null);
        String json = mapper.writeValueAsString(region);
        // Region.regionCode is a distraction for Case C — should still serialize correctly
        assertTrue(json.contains("regionCode"));
    }

    @Test
    void shouldSerializeOperatorUserDistraction() throws Exception {
        OperatorUser user = new OperatorUser("u1", "Alice", "NORTH", "111", "a@t.com", "expert");
        String json = mapper.writeValueAsString(user);
        // OperatorUser.regionCode is a distraction for Case C — should NOT be confused with DeviceInfo
        assertTrue(json.contains("regionCode"));
    }

    @Test
    void shouldDistinguishRegionCodeSemantics() throws Exception {
        // DeviceInfo.maintenanceRegionCode = device maintenance region (JSON: "maintenanceRegionCode")
        // Region.regionCode = region entity code (JSON: "regionCode")
        // OperatorUser.regionCode = operator's region (JSON: "regionCode")
        // After Case C rename, DeviceInfo uses a distinct JSON field name; Region/OperatorUser keep "regionCode"

        DeviceInfo deviceInfo = new DeviceInfo("d1", "BS", DeviceType.BASE_STATION,
                "Huawei", "DEVICE_REGION", "S1", "10.0.0.1", true);
        Region region = new Region("r1", "Region", "REGION_CODE", null);
        OperatorUser user = new OperatorUser("u1", "Bob", "USER_REGION", "111", "b@t.com", "expert");

        String deviceJson = mapper.writeValueAsString(deviceInfo);
        String regionJson = mapper.writeValueAsString(region);
        String userJson = mapper.writeValueAsString(user);

        // Each carries a distinct value across the three region-code concepts
        assertTrue(deviceJson.contains("DEVICE_REGION"));
        assertTrue(regionJson.contains("REGION_CODE"));
        assertTrue(userJson.contains("USER_REGION"));
    }
}
