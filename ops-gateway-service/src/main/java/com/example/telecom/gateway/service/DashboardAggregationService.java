package com.example.telecom.gateway.service;

import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.gateway.client.AlarmClient;
import com.example.telecom.gateway.client.DeviceClient;
import com.example.telecom.gateway.client.WorkOrderClient;
import com.example.telecom.gateway.dto.*;

import java.util.List;

/**
 * Aggregates data from device, alarm, and work order services for dashboard.
 * Key Case A flow node — aggregation endpoint.
 */
public class DashboardAggregationService {

    private final DeviceClient deviceClient;
    private final AlarmClient alarmClient;
    private final WorkOrderClient workOrderClient;

    public DashboardAggregationService(DeviceClient deviceClient,
                                        AlarmClient alarmClient,
                                        WorkOrderClient workOrderClient) {
        this.deviceClient = deviceClient;
        this.alarmClient = alarmClient;
        this.workOrderClient = workOrderClient;
    }

    public DeviceHealthSummary aggregateDeviceHealth(String regionCode) {
        List<DeviceInfo> devices = deviceClient.fetchDeviceSummary();
        DeviceHealthSummary summary = new DeviceHealthSummary();
        summary.setTotalDevices(devices.size());
        summary.setActiveDevices(devices.stream().filter(DeviceInfo::isActive).count());
        summary.setUnhealthyDevices(0);
        summary.setRegionCode(regionCode);
        return summary;
    }

    public AlarmSummary aggregateAlarms(String regionCode) {
        List<AlarmRecord> alarms = alarmClient.fetchActiveAlarmSummary();
        AlarmSummary summary = new AlarmSummary();
        summary.setTotalAlarms(alarms.size());
        summary.setCriticalAlarms(alarms.stream().filter(a -> a.getSeverity().name().equals("CRITICAL")).count());
        summary.setMajorAlarms(alarms.stream().filter(a -> a.getSeverity().name().equals("MAJOR")).count());
        summary.setWarningAlarms(alarms.stream().filter(a -> a.getSeverity().name().equals("WARNING")).count());
        summary.setRegionCode(regionCode);
        return summary;
    }

    public WorkOrderSummary aggregateWorkOrders(String regionCode) {
        List<WorkOrder> workOrders = workOrderClient.fetchOpenWorkOrderSummary();
        WorkOrderSummary summary = new WorkOrderSummary();
        summary.setOpenWorkOrders(workOrders.size());
        summary.setInProgressWorkOrders(0);
        summary.setResolvedToday(0);
        summary.setSlaBreached(0);
        summary.setRegionCode(regionCode);
        return summary;
    }
}
