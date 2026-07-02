package com.example.telecom.alarm.repository;

import com.example.telecom.common.maintenance.MaintenanceWindow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MaintenanceWindowRepository {
    private final Map<String, MaintenanceWindow> windows = new ConcurrentHashMap<>();

    public MaintenanceWindow save(MaintenanceWindow window) {
        windows.put(window.getWindowId(), window);
        return window;
    }

    public Optional<MaintenanceWindow> findById(String windowId) {
        return Optional.ofNullable(windows.get(windowId));
    }

    public List<MaintenanceWindow> findByDeviceId(String deviceId) {
        return windows.values().stream()
                .filter(w -> deviceId.equals(w.getDeviceId()))
                .toList();
    }

    public List<MaintenanceWindow> findByRegionCode(String regionCode) {
        return windows.values().stream()
                .filter(w -> regionCode.equals(w.getRegionCode()))
                .toList();
    }

    public List<MaintenanceWindow> findActiveWindows(long timestamp) {
        return windows.values().stream()
                .filter(w -> w.isActive() && w.coversTime(timestamp))
                .toList();
    }

    public List<MaintenanceWindow> findAll() {
        return new ArrayList<>(windows.values());
    }
}
