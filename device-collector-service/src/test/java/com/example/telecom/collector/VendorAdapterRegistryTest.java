package com.example.telecom.collector;

import com.example.telecom.collector.adapter.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VendorAdapterRegistryTest {

    private VendorAdapterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new VendorAdapterRegistry(List.of(
                new HuaweiVendorAdapter(), new ZteVendorAdapter(),
                new FiberHomeVendorAdapter(), new GenericSnmpVendorAdapter()
        ));
    }

    @Test
    void shouldResolveHuaweiAdapter() {
        VendorAdapter adapter = registry.resolve("Huawei");
        assertNotNull(adapter);
        assertTrue(adapter instanceof HuaweiVendorAdapter);
    }

    @Test
    void shouldResolveZteAdapter() {
        VendorAdapter adapter = registry.resolve("ZTE");
        assertNotNull(adapter);
    }

    @Test
    void shouldFallbackToDefaultForUnknownVendor() {
        VendorAdapter adapter = registry.resolve("UnknownVendor");
        assertNotNull(adapter);
    }
}
