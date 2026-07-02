package com.example.telecom.gateway;

import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.gateway.client.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for simulated cross-service clients in ops-gateway.
 */
class DashboardClientTest {

    @Test
    void deviceClientShouldReturnEmptyList() {
        DeviceClient client = new DeviceClient();
        List<DeviceInfo> devices = client.fetchDeviceSummary();
        assertNotNull(devices);
        assertTrue(devices.isEmpty());
    }

    @Test
    void deviceClientShouldReturnNullForSingleFetch() {
        DeviceClient client = new DeviceClient();
        assertNull(client.fetchDevice("any-device"));
    }

    @Test
    void alarmClientShouldReturnEmptyList() {
        AlarmClient client = new AlarmClient();
        List<AlarmRecord> alarms = client.fetchActiveAlarmSummary();
        assertNotNull(alarms);
        assertTrue(alarms.isEmpty());
    }

    @Test
    void alarmClientShouldReturnNullForSingleFetch() {
        AlarmClient client = new AlarmClient();
        assertNull(client.fetchAlarm("any-alarm"));
    }

    @Test
    void workOrderClientShouldReturnEmptyList() {
        WorkOrderClient client = new WorkOrderClient();
        List<WorkOrder> workOrders = client.fetchOpenWorkOrderSummary();
        assertNotNull(workOrders);
        assertTrue(workOrders.isEmpty());
    }

    @Test
    void workOrderClientShouldReturnNullForSingleFetch() {
        WorkOrderClient client = new WorkOrderClient();
        assertNull(client.fetchWorkOrder("any-wo"));
    }
}
