package com.example.telecom.gateway.client;

import com.example.telecom.common.device.DeviceInfo;

import java.util.List;

/**
 * Simulated client for device-collector-service.
 */
public class DeviceClient {

    public List<DeviceInfo> fetchDeviceSummary() {
        // In production, this would call device-collector-service REST API
        return List.of();
    }

    public DeviceInfo fetchDevice(String deviceId) {
        return null;
    }

    public List<DeviceInfo> getAllDevices() {
        return fetchDeviceSummary();
    }
}
