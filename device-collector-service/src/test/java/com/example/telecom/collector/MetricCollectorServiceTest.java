package com.example.telecom.collector;

import com.example.telecom.collector.adapter.*;
import com.example.telecom.collector.event.MetricEventPublisher;
import com.example.telecom.collector.mapper.DeviceMetricMapper;
import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.collector.service.*;
import com.example.telecom.collector.validator.DeviceMetricValidator;
import com.example.telecom.common.device.*;
import com.example.telecom.common.event.DomainEventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MetricCollectorServiceTest {

    private MetricCollectorService metricCollectorService;
    private DeviceMetricRepository deviceMetricRepository;
    private AtomicReference<DeviceMetricEvent> publishedEvent;

    @BeforeEach
    void setUp() {
        deviceMetricRepository = new DeviceMetricRepository();
        DeviceMetricValidator validator = new DeviceMetricValidator();
        DeviceMetricNormalizer normalizer = new DeviceMetricNormalizer();
        DeviceMetricMapper mapper = new DeviceMetricMapper();

        publishedEvent = new AtomicReference<>();
        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(DeviceMetricEvent event) { publishedEvent.set(event); }
            @Override public void publish(com.example.telecom.common.alarm.AlarmEvent event) {}
            @Override public void publish(com.example.telecom.common.workorder.WorkOrderEvent event) {}
        };
        MetricEventPublisher publisher = new MetricEventPublisher(bus);

        VendorAdapterRegistry adapterRegistry = new VendorAdapterRegistry(List.of(
                new HuaweiVendorAdapter(), new ZteVendorAdapter(),
                new FiberHomeVendorAdapter(), new GenericSnmpVendorAdapter()
        ));

        metricCollectorService = new MetricCollectorService(
                deviceMetricRepository, validator, normalizer, publisher, mapper, adapterRegistry);
    }

    @Test
    void shouldAcceptHuaweiCpuMetric() {
        DeviceInfo deviceInfo = new DeviceInfo("dev-1", "BS-01", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-1", "10.0.0.1", true);
        RawDeviceMetric raw = new RawDeviceMetric("dev-1", "Huawei", "cpu_usage_ratio",
                8500.0, "ratio", System.currentTimeMillis());

        DeviceMetric result = metricCollectorService.acceptMetric(raw, deviceInfo);

        assertNotNull(result);
        assertTrue(result.isNormalized());
        assertNotNull(publishedEvent.get());
        assertEquals("EAST", publishedEvent.get().getDeviceRegionCode());
    }

    @Test
    void shouldPropagateRegionCodeToMetricEvent() {
        DeviceInfo deviceInfo = new DeviceInfo("dev-2", "OLT-01", DeviceType.OLT,
                "ZTE", "SOUTH", "SITE-2", "10.0.0.2", true);
        RawDeviceMetric raw = new RawDeviceMetric("dev-2", "ZTE", "cpu_load",
                75.0, "%", System.currentTimeMillis());

        metricCollectorService.acceptMetric(raw, deviceInfo);

        assertNotNull(publishedEvent.get());
        // Case C verification: DeviceInfo.maintenanceRegionCode → DeviceMetricEvent.deviceRegionCode
        assertEquals("SOUTH", publishedEvent.get().getDeviceRegionCode());
    }
}
