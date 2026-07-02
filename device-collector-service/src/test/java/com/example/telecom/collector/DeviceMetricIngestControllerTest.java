package com.example.telecom.collector;

import com.example.telecom.collector.adapter.*;
import com.example.telecom.collector.controller.DeviceMetricIngestController;
import com.example.telecom.collector.dto.MetricIngestRequest;
import com.example.telecom.collector.event.MetricEventPublisher;
import com.example.telecom.collector.mapper.DeviceMetricMapper;
import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.collector.service.*;
import com.example.telecom.collector.validator.DeviceMetricValidator;
import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.device.*;
import com.example.telecom.common.event.DomainEventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMetricIngestControllerTest {

    private DeviceMetricIngestController controller;
    private DeviceRegistryRepository deviceRegistryRepository;

    @BeforeEach
    void setUp() {
        deviceRegistryRepository = new DeviceRegistryRepository();
        DeviceInfo deviceInfo = new DeviceInfo("dev-1", "BS-01", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-1", "10.0.0.1", true);
        deviceRegistryRepository.save(deviceInfo);

        DeviceRegistryService registryService = new DeviceRegistryService(deviceRegistryRepository);
        DeviceMetricRepository metricRepo = new DeviceMetricRepository();
        DeviceMetricValidator validator = new DeviceMetricValidator();
        DeviceMetricNormalizer normalizer = new DeviceMetricNormalizer();
        DeviceMetricMapper mapper = new DeviceMetricMapper();

        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(DeviceMetricEvent e) {}
            @Override public void publish(com.example.telecom.common.alarm.AlarmEvent e) {}
            @Override public void publish(com.example.telecom.common.workorder.WorkOrderEvent e) {}
        };
        MetricEventPublisher publisher = new MetricEventPublisher(bus);
        VendorAdapterRegistry adapterRegistry = new VendorAdapterRegistry(List.of(
                new HuaweiVendorAdapter(), new ZteVendorAdapter(),
                new FiberHomeVendorAdapter(), new GenericSnmpVendorAdapter()
        ));
        MetricCollectorService collectorService = new MetricCollectorService(
                metricRepo, validator, normalizer, publisher, mapper, adapterRegistry);
        CollectorAuditService auditService = new CollectorAuditService();

        controller = new DeviceMetricIngestController(collectorService, registryService, mapper, auditService);
    }

    @Test
    void shouldIngestMetricSuccessfully() {
        MetricIngestRequest request = new MetricIngestRequest();
        request.setVendor("Huawei");
        request.setRawMetricName("cpu_usage_ratio");
        request.setRawValue(8500.0);
        request.setTimestamp(System.currentTimeMillis());

        ApiResponse<com.example.telecom.collector.dto.DeviceMetricResponse> response =
                controller.ingestMetric("dev-1", request);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
    }
}
