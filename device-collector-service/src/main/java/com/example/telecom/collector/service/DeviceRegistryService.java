package com.example.telecom.collector.service;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.exception.ValidationException;

import java.util.List;
import java.util.UUID;

public class DeviceRegistryService {

    private final DeviceRegistryRepository deviceRegistryRepository;

    public DeviceRegistryService(DeviceRegistryRepository deviceRegistryRepository) {
        this.deviceRegistryRepository = deviceRegistryRepository;
    }

    public DeviceInfo register(DeviceRegistrationRequest request) {
        if (request.getDeviceName() == null || request.getDeviceName().isBlank()) {
            throw new ValidationException("deviceName", "Device name is required");
        }
        DeviceInfo deviceInfo = new DeviceInfo(
                UUID.randomUUID().toString(),
                request.getDeviceName(),
                request.getDeviceType(),
                request.getVendor(),
                request.getMaintenanceRegionCode(),
                request.getSiteCode(),
                request.getManagementIp(),
                true
        );
        return deviceRegistryRepository.save(deviceInfo);
    }

    public DeviceInfo findActiveDevice(String deviceId) {
        return deviceRegistryRepository.findActiveDevice(deviceId)
                .orElseThrow(() -> new ValidationException("deviceId", "Device not found or inactive: " + deviceId));
    }

    public List<DeviceInfo> findAllDevices() {
        return deviceRegistryRepository.findAll();
    }

    public List<DeviceInfo> findByRegionCode(String regionCode) {
        return deviceRegistryRepository.findByRegionCode(regionCode);
    }
}
