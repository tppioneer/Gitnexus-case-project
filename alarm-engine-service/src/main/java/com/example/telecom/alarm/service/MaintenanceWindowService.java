package com.example.telecom.alarm.service;

import com.example.telecom.alarm.repository.MaintenanceWindowRepository;
import com.example.telecom.common.maintenance.MaintenanceWindow;

import java.util.List;
import java.util.UUID;

public class MaintenanceWindowService {

    private final MaintenanceWindowRepository maintenanceWindowRepository;

    public MaintenanceWindowService(MaintenanceWindowRepository maintenanceWindowRepository) {
        this.maintenanceWindowRepository = maintenanceWindowRepository;
    }

    public MaintenanceWindow createWindow(String deviceId, String regionCode,
                                           long startTime, long endTime, String reason) {
        MaintenanceWindow window = new MaintenanceWindow(
                UUID.randomUUID().toString(), deviceId, regionCode,
                startTime, endTime, reason, true);
        return maintenanceWindowRepository.save(window);
    }

    public boolean isDeviceInMaintenance(String deviceId, long timestamp) {
        List<MaintenanceWindow> windows = maintenanceWindowRepository.findByDeviceId(deviceId);
        return windows.stream().anyMatch(w -> w.isActive() && w.coversTime(timestamp));
    }

    public List<MaintenanceWindow> findActiveWindows(long timestamp) {
        return maintenanceWindowRepository.findActiveWindows(timestamp);
    }

    public List<MaintenanceWindow> resolve(String regionCode) {
        return maintenanceWindowRepository.findByRegionCode(regionCode);
    }
}
