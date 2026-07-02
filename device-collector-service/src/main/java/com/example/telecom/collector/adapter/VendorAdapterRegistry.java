package com.example.telecom.collector.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VendorAdapterRegistry {
    private final Map<String, VendorAdapter> adapterMap;
    private final VendorAdapter defaultAdapter;

    public VendorAdapterRegistry(List<VendorAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(
                        a -> {
                            String simpleName = a.getClass().getSimpleName().replace("VendorAdapter", "");
                            // Map class names to standard vendor names
                            return switch (simpleName) {
                                case "Huawei" -> "Huawei";
                                case "Zte" -> "ZTE";
                                case "FiberHome" -> "FiberHome";
                                case "GenericSnmp" -> "Generic";
                                default -> simpleName;
                            };
                        },
                        a -> a
                ));
        this.defaultAdapter = adapters.stream()
                .filter(a -> a instanceof GenericSnmpVendorAdapter)
                .findFirst()
                .orElse(adapters.get(0));
    }

    public VendorAdapter resolve(String vendor) {
        return adapterMap.getOrDefault(vendor, defaultAdapter);
    }
}
