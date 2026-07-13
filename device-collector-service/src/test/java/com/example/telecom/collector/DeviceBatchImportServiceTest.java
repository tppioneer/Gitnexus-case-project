package com.example.telecom.collector;

import com.example.telecom.collector.dto.DeviceBatchImportRequest;
import com.example.telecom.collector.dto.DeviceBatchImportResponse;
import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.repository.DeviceRegistryRepository;
import com.example.telecom.collector.service.DeviceBatchImportService;
import com.example.telecom.collector.service.DeviceImportAuditService;
import com.example.telecom.collector.service.DeviceRegistryService;
import com.example.telecom.collector.validator.DeviceImportValidator;
import com.example.telecom.collector.validator.DeviceRegistrationValidator;
import com.example.telecom.common.api.OperationResult;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeviceBatchImportServiceTest {

    private DeviceBatchImportService batchImportService;
    private DeviceRegistryService registryService;
    private DeviceRegistryRepository registryRepository;
    private DeviceImportValidator importValidator;
    private DeviceImportAuditService auditService;

    @BeforeEach
    void setUp() {
        registryRepository = new DeviceRegistryRepository();
        registryService = new DeviceRegistryService(registryRepository);
        importValidator = new DeviceImportValidator();
        batchImportService = new DeviceBatchImportService(registryService, registryRepository,
                new DeviceRegistrationValidator());
        auditService = new DeviceImportAuditService();
    }

    @Test
    void shouldImportMultipleDevices() {
        List<DeviceRegistrationRequest> requests = List.of(
                createRequest("Device-A", DeviceType.BASE_STATION, "Huawei", "EAST", "S1", "10.0.0.1"),
                createRequest("Device-B", DeviceType.OLT, "ZTE", "WEST", "S2", "10.0.0.2"),
                createRequest("Device-C", DeviceType.ROUTER, "Cisco", "EAST", "S3", "10.0.0.3")
        );

        OperationResult result = batchImportService.importDevices(requests);

        assertTrue(result.isSuccess());
        assertEquals(3, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(3, registryRepository.findAll().size());
    }

    @Test
    void shouldHandleDuplicateDeviceNames() {
        List<DeviceRegistrationRequest> requests = List.of(
                createRequest("Duplicate-Dev", DeviceType.BASE_STATION, "Huawei", "EAST", "S1", "10.0.0.1"),
                createRequest("Duplicate-Dev", DeviceType.OLT, "ZTE", "WEST", "S2", "10.0.0.2")
        );

        OperationResult result = batchImportService.importDevices(requests);

        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void shouldImportEmptyListGracefully() {
        OperationResult result = batchImportService.importDevices(List.of());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessCount());
    }

    @Test
    void shouldAuditImportOperations() {
        List<DeviceRegistrationRequest> requests = List.of(
                createRequest("Audit-1", DeviceType.SWITCH, "Cisco", "EAST", "S1", "10.0.0.1"),
                createRequest("Audit-2", DeviceType.ROUTER, "Huawei", "WEST", "S2", "10.0.0.2")
        );

        OperationResult result = batchImportService.importDevices(requests);
        auditService.recordImport("batch-001", "CSV", requests.size(),
                result.getSuccessCount(), result.getFailureCount(), result.getErrors());

        List<DeviceImportAuditService.ImportAuditRecord> history = auditService.getImportHistory();
        assertEquals(1, history.size());
        assertEquals("batch-001", history.get(0).getBatchId());
        assertEquals(2, history.get(0).getTotalRecords());
    }

    @Test
    void shouldHandlePartiallyValidBatch() {
        List<DeviceRegistrationRequest> requests = new ArrayList<>();
        requests.add(createRequest("Valid-1", DeviceType.BASE_STATION, "Huawei", "EAST", "S1", "10.0.0.1"));
        requests.add(createRequest("", DeviceType.OLT, "ZTE", "WEST", "S2", "10.0.0.2"));
        requests.add(createRequest("Valid-2", DeviceType.ROUTER, "Cisco", "EAST", "S3", "10.0.0.3"));

        OperationResult result = batchImportService.importDevices(requests);

        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
    }

    @Test
    void shouldFindDevicesByVendor() {
        batchImportService.importDevices(List.of(
                createRequest("HW-1", DeviceType.BASE_STATION, "Huawei", "EAST", "S1", "10.0.0.1"),
                createRequest("ZTE-1", DeviceType.OLT, "ZTE", "WEST", "S2", "10.0.0.2"),
                createRequest("HW-2", DeviceType.ROUTER, "Huawei", "EAST", "S3", "10.0.0.3")
        ));

        List<DeviceInfo> huaweiDevices = batchImportService.findDevicesByVendor("Huawei");
        assertEquals(2, huaweiDevices.size());

        List<DeviceInfo> zteDevices = batchImportService.findDevicesByVendor("ZTE");
        assertEquals(1, zteDevices.size());
    }

    private DeviceRegistrationRequest createRequest(String name, DeviceType type, String vendor,
                                                     String regionCode, String siteCode, String ip) {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceName(name);
        request.setDeviceType(type);
        request.setVendor(vendor);
        request.setRegionCode(regionCode);
        request.setSiteCode(siteCode);
        request.setManagementIp(ip);
        return request;
    }
}
