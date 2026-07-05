package com.example.telecom.collector.repository;

import com.example.telecom.common.device.DeviceInfo;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceRegistryRepository {
    private final Map<String, DeviceInfo> devices = new ConcurrentHashMap<>();

    public DeviceInfo save(DeviceInfo deviceInfo) {
        devices.put(deviceInfo.getDeviceId(), deviceInfo);
        return deviceInfo;
    }

    public Optional<DeviceInfo> findById(String deviceId) {
        return Optional.ofNullable(devices.get(deviceId));
    }

    public Optional<DeviceInfo> findActiveDevice(String deviceId) {
        return findById(deviceId).filter(DeviceInfo::isActive);
    }

    public List<DeviceInfo> findAll() {
        return new ArrayList<>(devices.values());
    }

    public List<DeviceInfo> findByRegionCode(String regionCode) {
        return devices.values().stream()
                .filter(d -> regionCode.equals(d.getMaintenanceRegionCode()))
                .toList();
    }
}
