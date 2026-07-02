package com.example.telecom.collector.adapter;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.RawDeviceMetric;

import java.util.Map;

public class HuaweiVendorAlarmAdapter implements VendorAlarmAdapter {

    @Override
    public Map<String, Object> normalize(RawDeviceMetric rawMetric, DeviceInfo deviceInfo) {
        return Map.of(
                "vendor", "Huawei",
                "deviceId", rawMetric.getDeviceId(),
                "rawMetricName", rawMetric.getRawMetricName(),
                "rawValue", rawMetric.getRawValue(),
                "normalized", true
        );
    }
}
