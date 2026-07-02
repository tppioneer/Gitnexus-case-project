package com.example.telecom.gateway.service;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.gateway.client.DeviceClient;

import java.util.List;

/**
 * Evaluates device health for dashboard. Has evaluate() methods.
 * Case B NOISE — does NOT implement RuleEvaluator.
 */
public class DeviceHealthEvaluator {

    private final DeviceClient deviceClient;

    public DeviceHealthEvaluator(DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    public String evaluate(String regionCode) {
        List<DeviceInfo> devices = deviceClient.fetchDeviceSummary();
        long active = devices.stream().filter(DeviceInfo::isActive).count();
        double rate = devices.isEmpty() ? 100.0 : (double) active / devices.size() * 100.0;
        return String.format("Region %s device health: %.1f%% active (%d/%d)",
                regionCode, rate, active, devices.size());
    }

    public int evaluate(List<DeviceInfo> devices) {
        if (devices.isEmpty()) return 100;
        long active = devices.stream().filter(DeviceInfo::isActive).count();
        return (int) (active * 100 / devices.size());
    }
}
