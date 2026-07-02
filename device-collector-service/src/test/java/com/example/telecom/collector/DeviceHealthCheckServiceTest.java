package com.example.telecom.collector;

import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.collector.service.DeviceHealthCheckService;
import com.example.telecom.collector.service.DeviceHealthCheckService.HealthStatus;
import com.example.telecom.common.device.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DeviceHealthCheckServiceTest {

    private DeviceHealthCheckService healthCheckService;
    private DeviceRegistryRepository registryRepository;
    private DeviceMetricRepository metricRepository;

    @BeforeEach
    void setUp() {
        registryRepository = new DeviceRegistryRepository();
        metricRepository = new DeviceMetricRepository();
        healthCheckService = new DeviceHealthCheckService(registryRepository, metricRepository);

        // Register several devices in different states
        registryRepository.save(new DeviceInfo("dev-healthy", "BS-Healthy", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S1", "10.0.1.1", true));
        registryRepository.save(new DeviceInfo("dev-cpu-high", "BS-CPU", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S2", "10.0.1.2", true));
        registryRepository.save(new DeviceInfo("dev-inactive", "BS-Off", DeviceType.BASE_STATION,
                "ZTE", "EAST", "S3", "10.0.1.3", false));
        registryRepository.save(new DeviceInfo("dev-west", "OLT-West", DeviceType.OLT,
                "FiberHome", "WEST", "S4", "10.0.2.1", true));

        // Add healthy metrics for healthy device
        DeviceMetric healthyMetric = new DeviceMetric("m1", "dev-healthy", MetricType.CPU_USAGE,
                45.0, "%", Instant.now(), "Huawei");
        healthyMetric.setNormalized(true);
        metricRepository.save(healthyMetric);

        // Add unhealthy metrics for CPU-high device
        DeviceMetric cpuHigh = new DeviceMetric("m2", "dev-cpu-high", MetricType.CPU_USAGE,
                95.0, "%", Instant.now(), "Huawei");
        cpuHigh.setNormalized(true);
        metricRepository.save(cpuHigh);

        DeviceMetric lossHigh = new DeviceMetric("m3", "dev-cpu-high", MetricType.PACKET_LOSS,
                12.0, "%", Instant.now(), "Huawei");
        lossHigh.setNormalized(true);
        metricRepository.save(lossHigh);
    }

    @Test
    void shouldEvaluateHealthyDevice() {
        assertEquals(HealthStatus.HEALTHY, healthCheckService.evaluateDeviceHealth("dev-healthy"));
    }

    @Test
    void shouldEvaluateUnhealthyDevice() {
        assertEquals(HealthStatus.UNHEALTHY, healthCheckService.evaluateDeviceHealth("dev-cpu-high"));
    }

    @Test
    void shouldReturnUnknownForNonExistentDevice() {
        assertEquals(HealthStatus.UNKNOWN, healthCheckService.evaluateDeviceHealth("dev-nonexistent"));
    }

    @Test
    void shouldReturnUnknownForInactiveDevice() {
        assertEquals(HealthStatus.UNKNOWN, healthCheckService.evaluateDeviceHealth("dev-inactive"));
    }

    @Test
    void shouldEvaluateRegionHealth() {
        Map<String, HealthStatus> regionHealth = healthCheckService.evaluateRegionHealth("EAST");
        assertTrue(regionHealth.containsKey("dev-healthy"));
        assertTrue(regionHealth.containsKey("dev-cpu-high"));
        // Inactive devices should be excluded
        assertFalse(regionHealth.containsKey("dev-inactive"));
    }

    @Test
    void shouldCountHealthByRegion() {
        Map<String, Long> counts = healthCheckService.countHealthByRegion("EAST");
        assertEquals(1, counts.getOrDefault("healthy", 0L));
        assertEquals(1, counts.getOrDefault("unhealthy", 0L));
    }

    @Test
    void shouldGenerateHealthReport() {
        String report = healthCheckService.generateHealthReport("EAST");
        assertTrue(report.contains("EAST"));
        assertTrue(report.contains("healthy=1"));
        assertTrue(report.contains("unhealthy=1"));
    }
}
