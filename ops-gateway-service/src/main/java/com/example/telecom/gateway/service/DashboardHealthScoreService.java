package com.example.telecom.gateway.service;

import com.example.telecom.gateway.client.*;
import com.example.telecom.gateway.dto.*;

public class DashboardHealthScoreService {

    private final DeviceClient deviceClient;
    private final AlarmClient alarmClient;
    private final WorkOrderClient workOrderClient;

    public DashboardHealthScoreService(DeviceClient deviceClient, AlarmClient alarmClient,
                                        WorkOrderClient workOrderClient) {
        this.deviceClient = deviceClient;
        this.alarmClient = alarmClient;
        this.workOrderClient = workOrderClient;
    }

    public int calculateOverallScore(String regionCode) {
        int deviceScore = calculateDeviceScore();
        int alarmScore = calculateAlarmScore();
        int woScore = calculateWorkOrderScore();
        return (deviceScore + alarmScore + woScore) / 3;
    }

    private int calculateDeviceScore() {
        var devices = deviceClient.fetchDeviceSummary();
        if (devices.isEmpty()) return 100;
        long activeCount = devices.stream().filter(
                d -> d.isActive()).count();
        return (int) (activeCount * 100 / Math.max(devices.size(), 1));
    }

    private int calculateAlarmScore() {
        var alarms = alarmClient.fetchActiveAlarmSummary();
        return alarms.isEmpty() ? 100 : Math.max(0, 100 - alarms.size() * 5);
    }

    private int calculateWorkOrderScore() {
        var wos = workOrderClient.fetchOpenWorkOrderSummary();
        return wos.isEmpty() ? 100 : Math.max(0, 100 - wos.size() * 3);
    }
}
