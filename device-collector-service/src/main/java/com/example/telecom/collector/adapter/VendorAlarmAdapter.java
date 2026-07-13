package com.example.telecom.collector.adapter;

import com.example.telecom.collector.dto.NormalizedAlarm;

public interface VendorAlarmAdapter {

    NormalizedAlarm normalize(String rawAlarm);

    String getVendorType();
}
