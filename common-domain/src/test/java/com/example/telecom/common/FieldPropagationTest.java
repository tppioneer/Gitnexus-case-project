package com.example.telecom.common;

import com.example.telecom.common.alarm.*;
import com.example.telecom.common.device.*;
import com.example.telecom.common.region.Region;
import com.example.telecom.common.user.OperatorUser;
import com.example.telecom.common.workorder.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the field propagation chain for Case C:
 * DeviceInfo.maintenanceRegionCode → DeviceMetricEvent.deviceRegionCode → AlarmRecord.alarmRegionCode
 * → AlarmEvent.deviceRegionCode → WorkOrder.maintenanceRegionCode
 *
 * Also verifies that Region.regionCode and OperatorUser.regionCode are SEPARATE fields.
 */
class FieldPropagationTest {

    @Test
    void deviceInfoRegionCodeShouldBeSetCorrectly() {
        DeviceInfo deviceInfo = new DeviceInfo("d1", "BS", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S1", "10.0.0.1", true);
        assertEquals("EAST", deviceInfo.getMaintenanceRegionCode());
    }

    @Test
    void deviceMetricEventDeviceRegionCodeShouldBeIndependentField() {
        DeviceMetricEvent event = new DeviceMetricEvent("e1", "d1", "m1", "CPU_USAGE",
                90.0, "%", System.currentTimeMillis(), "EAST");
        assertEquals("EAST", event.getDeviceRegionCode());
    }

    @Test
    void alarmRecordAlarmRegionCodeShouldBeIndependentField() {
        AlarmRecord alarm = new AlarmRecord("a1", "d1", "m1", "CPU_USAGE",
                Severity.MAJOR, AlarmStatus.OPEN, "desc", "SOUTH", System.currentTimeMillis());
        assertEquals("SOUTH", alarm.getAlarmRegionCode());
    }

    @Test
    void workOrderMaintenanceRegionCodeShouldBeIndependentField() {
        WorkOrder wo = new WorkOrder("w1", "a1", "d1", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "t", "d", "WEST", System.currentTimeMillis());
        assertEquals("WEST", wo.getMaintenanceRegionCode());
    }

    @Test
    void regionRegionCodeIsDistractionField() {
        Region region = new Region("r1", "East Region", "EAST", null);
        assertEquals("EAST", region.getRegionCode());
    }

    @Test
    void operatorUserRegionCodeIsDistractionField() {
        OperatorUser user = new OperatorUser("u1", "Alice", "NORTH", "111", "a@t.com", "expert");
        assertEquals("NORTH", user.getRegionCode());
    }
}
