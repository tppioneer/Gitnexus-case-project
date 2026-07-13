package com.example.telecom.gateway.service;

import com.example.telecom.gateway.client.AlarmClient;
import com.example.telecom.gateway.client.DeviceClient;
import com.example.telecom.gateway.client.WorkOrderClient;
import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.workorder.WorkOrder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
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

    public Map<String, Object> getRegionDrilldown(String regionCode) {
        List<DeviceInfo> devices = deviceClient.getAllDevices();
        List<AlarmRecord> alarms = alarmClient.getAllAlarms();

        Map<String, Object> drilldown = new LinkedHashMap<>();
        drilldown.put("regionCode", regionCode);
        drilldown.put("deviceCount", devices.size());
        drilldown.put("alarmCount", alarms.size());
        drilldown.put("workOrderCount", workOrderClient.getAllWorkOrders().size());

        Map<String, Object> metrics = aggregateMetrics(devices, alarms);
        drilldown.put("metrics", metrics);
        return drilldown;
    }

    public Map<String, Object> getDeviceDrilldown(String deviceId) {
        List<AlarmRecord> alarms = alarmClient.getAllAlarms();

        Map<String, Object> drilldown = new LinkedHashMap<>();
        drilldown.put("deviceId", deviceId);
        drilldown.put("alarmCount", alarms.size());
        drilldown.put("relatedAlarms", alarms.stream().limit(20).collect(Collectors.toList()));

        Map<String, Object> metrics = aggregateMetrics(Collections.emptyList(), alarms);
        drilldown.put("metrics", metrics);
        return drilldown;
    }

    public Map<String, Object> getAlarmDrilldown(String alarmId) {
        Map<String, Object> drilldown = new LinkedHashMap<>();
        drilldown.put("alarmId", alarmId);
        drilldown.put("deviceCount", deviceClient.getAllDevices().size());
        drilldown.put("workOrderCount", workOrderClient.getAllWorkOrders().size());
        return drilldown;
    }

    public Map<String, Object> getTopologyDrilldown(String nodeId) {
        Map<String, Object> drilldown = new LinkedHashMap<>();
        drilldown.put("nodeId", nodeId);
        drilldown.put("connectedDevices", deviceClient.getAllDevices().size());
        drilldown.put("connectionCount", deviceClient.getAllDevices().size());

        Map<String, Object> trafficMetrics = new LinkedHashMap<>();
        trafficMetrics.put("inboundBandwidth", 1024.0);
        trafficMetrics.put("outboundBandwidth", 512.0);
        trafficMetrics.put("packetLoss", 0.01);
        trafficMetrics.put("latency", 5.2);
        drilldown.put("trafficMetrics", trafficMetrics);
        return drilldown;
    }

    private Map<String, Object> aggregateMetrics(List<DeviceInfo> devices, List<AlarmRecord> alarms) {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalDevices", devices.size());
        metrics.put("totalAlarms", alarms.size());
        metrics.put("averageHealth", 85.0);
        metrics.put("availability", 99.5);
        return metrics;
    }
}
