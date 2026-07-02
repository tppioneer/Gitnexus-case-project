package com.example.telecom.collector.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VendorAlarmAdapterRegistry {
    private final Map<String, VendorAlarmAdapter> adapterMap;

    public VendorAlarmAdapterRegistry(List<VendorAlarmAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(
                        a -> a.getClass().getSimpleName().replace("VendorAlarmAdapter", ""),
                        a -> a));
    }

    public VendorAlarmAdapter resolve(String vendor) {
        return adapterMap.getOrDefault(vendor, adapterMap.values().iterator().next());
    }
}
