package com.example.telecom.collector;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.collector.service.DeviceRegistryService;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceType;
import com.example.telecom.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeviceRegistryServiceTest {

    private DeviceRegistryService registryService;
    private DeviceRegistryRepository registryRepository;

    @BeforeEach
    void setUp() {
        registryRepository = new DeviceRegistryRepository();
        registryService = new DeviceRegistryService(registryRepository);
    }

    @Test
    void shouldRegisterDevice() {
        DeviceRegistrationRequest request = createRequest("BS-East-01", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-001", "10.0.0.1");
        DeviceInfo device = registryService.register(request);

        assertNotNull(device.getDeviceId());
        assertEquals("BS-East-01", device.getDeviceName());
        assertEquals("EAST", device.getMaintenanceRegionCode());
        assertTrue(device.isActive());
    }

    @Test
    void shouldRejectEmptyDeviceName() {
        DeviceRegistrationRequest request = createRequest("", DeviceType.BASE_STATION,
                "Huawei", "EAST", "SITE-001", "10.0.0.1");
        assertThrows(ValidationException.class, () -> registryService.register(request));
    }

    @Test
    void shouldRejectNullDeviceName() {
        DeviceRegistrationRequest request = createRequest(null, DeviceType.ROUTER,
                "Cisco", "WEST", "SITE-002", "10.0.0.2");
        assertThrows(ValidationException.class, () -> registryService.register(request));
    }

    @Test
    void shouldFindActiveDevice() {
        DeviceRegistrationRequest request = createRequest("OLT-West-01", DeviceType.OLT,
                "ZTE", "WEST", "SITE-003", "10.0.2.1");
        DeviceInfo saved = registryService.register(request);

        DeviceInfo found = registryService.findActiveDevice(saved.getDeviceId());
        assertEquals("OLT-West-01", found.getDeviceName());
    }

    @Test
    void shouldThrowForInactiveDevice() {
        DeviceRegistrationRequest request = createRequest("SW-South-01", DeviceType.SWITCH,
                "Cisco", "SOUTH", "SITE-004", "10.0.3.1");
        DeviceInfo saved = registryService.register(request);

        // Manually deactivate
        saved.setActive(false);
        registryRepository.save(saved);

        assertThrows(ValidationException.class, () -> registryService.findActiveDevice(saved.getDeviceId()));
    }

    @Test
    void shouldFindAllDevices() {
        registryService.register(createRequest("Dev-1", DeviceType.BASE_STATION, "Huawei",
                "EAST", "S1", "10.0.0.1"));
        registryService.register(createRequest("Dev-2", DeviceType.OLT, "ZTE",
                "WEST", "S2", "10.0.0.2"));
        registryService.register(createRequest("Dev-3", DeviceType.ROUTER, "Cisco",
                "EAST", "S3", "10.0.0.3"));

        List<DeviceInfo> all = registryService.findAllDevices();
        assertEquals(3, all.size());
    }

    @Test
    void shouldFindByRegionCode() {
        registryService.register(createRequest("Dev-East-1", DeviceType.BASE_STATION, "Huawei",
                "EAST", "S1", "10.0.0.1"));
        registryService.register(createRequest("Dev-East-2", DeviceType.OLT, "ZTE",
                "EAST", "S2", "10.0.0.2"));
        registryService.register(createRequest("Dev-West-1", DeviceType.ROUTER, "Cisco",
                "WEST", "S3", "10.0.0.3"));

        assertEquals(2, registryService.findByRegionCode("EAST").size());
        assertEquals(1, registryService.findByRegionCode("WEST").size());
    }

    private DeviceRegistrationRequest createRequest(String name, DeviceType type, String vendor,
                                                     String regionCode, String siteCode, String ip) {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceName(name);
        request.setDeviceType(type);
        request.setVendor(vendor);
        request.setMaintenanceRegionCode(regionCode);
        request.setSiteCode(siteCode);
        request.setManagementIp(ip);
        return request;
    }
}
