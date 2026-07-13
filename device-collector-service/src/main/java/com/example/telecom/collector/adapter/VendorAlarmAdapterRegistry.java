package com.example.telecom.collector.adapter;

import com.example.telecom.collector.dto.NormalizedAlarm;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VendorAlarmAdapterRegistry {

    private final Map<String, VendorAlarmAdapter> adapterMap = new ConcurrentHashMap<>();

    public VendorAlarmAdapterRegistry() {
    }

    public VendorAlarmAdapterRegistry(List<VendorAlarmAdapter> adapters) {
        if (adapters != null) {
            for (VendorAlarmAdapter adapter : adapters) {
                register(adapter);
            }
        }
    }

    public void register(VendorAlarmAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Adapter must not be null");
        }
        adapterMap.put(adapter.getVendorType(), adapter);
    }

    public VendorAlarmAdapter resolve(String vendorType) {
        if (vendorType == null || vendorType.isBlank()) {
            throw new IllegalArgumentException("Vendor type must not be null or blank");
        }
        return adapterMap.get(vendorType);
    }

    public List<VendorAlarmAdapter> getAllAdapters() {
        return new ArrayList<>(adapterMap.values());
    }

    public NormalizedAlarm normalize(String vendorType, String rawAlarm) {
        VendorAlarmAdapter adapter = resolve(vendorType);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for vendor type: " + vendorType);
        }
        return adapter.normalize(rawAlarm);
    }

    public int size() {
        return adapterMap.size();
    }

    public boolean containsVendor(String vendorType) {
        return adapterMap.containsKey(vendorType);
    }
}
