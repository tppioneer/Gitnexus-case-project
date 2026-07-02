package com.example.telecom.gateway.service;

import com.example.telecom.gateway.client.*;
import com.example.telecom.gateway.dto.*;

import java.util.*;

public class DashboardDrilldownService {

    private final DeviceClient deviceClient;
    private final AlarmClient alarmClient;
    private final WorkOrderClient workOrderClient;

    public DashboardDrilldownService(DeviceClient deviceClient, AlarmClient alarmClient,
                                      WorkOrderClient workOrderClient) {
        this.deviceClient = deviceClient;
        this.alarmClient = alarmClient;
        this.workOrderClient = workOrderClient;
    }

    public Map<String, Object> drillDownRegion(String regionCode) {
        var devices = deviceClient.fetchDeviceSummary();
        var alarms = alarmClient.fetchActiveAlarmSummary();
        var workOrders = workOrderClient.fetchOpenWorkOrderSummary();

        Map<String, Object> drilldown = new LinkedHashMap<>();
        drilldown.put("regionCode", regionCode);
        drilldown.put("deviceCount", devices.size());
        drilldown.put("alarmCount", alarms.size());
        drilldown.put("workOrderCount", workOrders.size());
        drilldown.put("alarmsBySeverity", groupAlarmsBySeverity(alarms));
        drilldown.put("workOrdersByStatus", groupWorkOrdersByStatus(workOrders));
        return drilldown;
    }

    private Map<String, Long> groupAlarmsBySeverity(List<?> alarms) {
        return Map.of("CRITICAL", 0L, "MAJOR", 0L, "WARNING", 0L, "INFO", 0L);
    }

    private Map<String, Long> groupWorkOrdersByStatus(List<?> workOrders) {
        return Map.of("OPEN", 0L, "IN_PROGRESS", 0L, "RESOLVED", 0L);
    }
}
