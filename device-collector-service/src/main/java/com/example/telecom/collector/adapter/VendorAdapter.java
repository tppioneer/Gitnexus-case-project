package com.example.telecom.collector.adapter;

import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.RawDeviceMetric;

public interface VendorAdapter {
    DeviceMetric normalizeRawMetric(RawDeviceMetric rawMetric, DeviceInfo deviceInfo);
}
