package com.example.telecom.collector;

import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeviceRegistryRepositoryTest {

    private DeviceRegistryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DeviceRegistryRepository();

        repository.save(new DeviceInfo("d1", "BS-East", DeviceType.BASE_STATION,
                "Huawei", "EAST", "S1", "10.0.0.1", true));
        repository.save(new DeviceInfo("d2", "OLT-West", DeviceType.OLT,
                "ZTE", "WEST", "S2", "10.0.0.2", true));
        repository.save(new DeviceInfo("d3", "SW-East", DeviceType.SWITCH,
                "Cisco", "EAST", "S3", "10.0.0.3", false));
        repository.save(new DeviceInfo("d4", "RT-South", DeviceType.ROUTER,
                "Huawei", "SOUTH", "S4", "10.0.0.4", true));
    }

    @Test
    void shouldFindById() {
        assertTrue(repository.findById("d1").isPresent());
        assertEquals("BS-East", repository.findById("d1").get().getDeviceName());
    }

    @Test
    void shouldFindActiveDevice() {
        assertTrue(repository.findActiveDevice("d1").isPresent());
        assertFalse(repository.findActiveDevice("d3").isPresent());
    }

    @Test
    void shouldFindAll() {
        assertEquals(4, repository.findAll().size());
    }

    @Test
    void shouldFindByRegionCode() {
        List<DeviceInfo> eastDevices = repository.findByRegionCode("EAST");
        assertEquals(2, eastDevices.size());
        assertEquals(1, repository.findByRegionCode("WEST").size());
        assertEquals(1, repository.findByRegionCode("SOUTH").size());
    }

    @Test
    void shouldReturnEmptyForMissingDevice() {
        assertTrue(repository.findById("nonexistent").isEmpty());
    }
}
