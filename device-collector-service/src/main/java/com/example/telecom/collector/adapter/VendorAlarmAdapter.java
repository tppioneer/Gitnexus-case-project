package com.example.telecom.collector.adapter;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.RawDeviceMetric;

import java.util.Map;

public interface VendorAlarmAdapter {
    Map<String, Object> normalize(RawDeviceMetric rawMetric, DeviceInfo deviceInfo);
}
