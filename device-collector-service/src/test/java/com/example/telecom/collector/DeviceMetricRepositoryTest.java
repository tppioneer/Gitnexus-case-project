package com.example.telecom.collector;

import com.example.telecom.collector.repository.DeviceMetricRepository;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMetricRepositoryTest {

    private DeviceMetricRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DeviceMetricRepository();

        repository.save(new DeviceMetric("m1", "dev-1", MetricType.CPU_USAGE, 50.0,
                "%", Instant.now(), "Huawei"));
        repository.save(new DeviceMetric("m2", "dev-1", MetricType.MEMORY_USAGE, 60.0,
                "%", Instant.now(), "Huawei"));
        repository.save(new DeviceMetric("m3", "dev-2", MetricType.CPU_USAGE, 30.0,
                "%", Instant.now(), "ZTE"));
    }

    @Test
    void shouldFindById() {
        assertTrue(repository.findById("m1").isPresent());
        assertEquals(50.0, repository.findById("m1").get().getValue());
    }

    @Test
    void shouldFindByDeviceId() {
        List<DeviceMetric> dev1Metrics = repository.findByDeviceId("dev-1");
        assertEquals(2, dev1Metrics.size());
    }

    @Test
    void shouldFindAll() {
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void shouldReturnEmptyForMissingId() {
        assertTrue(repository.findById("nonexistent").isEmpty());
    }

    @Test
    void shouldReturnEmptyListForDeviceWithNoMetrics() {
        assertTrue(repository.findByDeviceId("dev-nonexistent").isEmpty());
    }
}
