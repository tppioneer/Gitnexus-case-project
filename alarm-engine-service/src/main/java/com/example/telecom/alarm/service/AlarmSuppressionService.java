package com.example.telecom.alarm.service;

import com.example.telecom.common.alarm.AlarmRecord;
import com.example.telecom.common.maintenance.MaintenanceWindow;

import java.util.List;

public class AlarmSuppressionService {

    private final MaintenanceWindowService maintenanceWindowService;

    public AlarmSuppressionService(MaintenanceWindowService maintenanceWindowService) {
        this.maintenanceWindowService = maintenanceWindowService;
    }

    public boolean shouldSuppress(AlarmRecord alarm) {
        return maintenanceWindowService.isDeviceInMaintenance(
                alarm.getDeviceId(), System.currentTimeMillis());
    }

    public List<AlarmRecord> filterSuppressed(List<AlarmRecord> alarms) {
        return alarms.stream()
                .filter(a -> !shouldSuppress(a))
                .toList();
    }

    public String evaluate(AlarmRecord alarm) {
        if (shouldSuppress(alarm)) {
            return "SUPPRESS: Alarm " + alarm.getAlarmId() + " is within maintenance window";
        }
        List<MaintenanceWindow> windows = maintenanceWindowService.findActiveWindows(
                System.currentTimeMillis());
        return "PROCESS: No active maintenance for device " + alarm.getDeviceId()
                + ", active windows: " + windows.size();
    }
}
