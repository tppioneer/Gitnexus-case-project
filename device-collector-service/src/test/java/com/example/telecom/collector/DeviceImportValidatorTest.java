package com.example.telecom.collector;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.validator.DeviceImportValidator;
import com.example.telecom.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeviceImportValidatorTest {

    private DeviceImportValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DeviceImportValidator();
    }

    @Test
    void shouldValidateValidDevice() {
        DeviceRegistrationRequest request = createRequest("Valid-Device-01", "Huawei",
                "EAST", "SITE-001", "10.0.0.1");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void shouldRejectEmptyDeviceName() {
        DeviceRegistrationRequest request = createRequest("", "Huawei", "EAST", "S1", "10.0.0.1");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectNullDeviceName() {
        DeviceRegistrationRequest request = createRequest(null, "ZTE", "WEST", "S2", "10.0.0.2");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectInvalidDeviceNameCharacters() {
        DeviceRegistrationRequest request = createRequest("Bad@Name!", "Cisco", "EAST", "S3", "10.0.0.3");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectInvalidIpAddress() {
        DeviceRegistrationRequest request = createRequest("Bad-IP-Dev", "Huawei",
                "EAST", "S4", "999.999.999.999");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectInvalidRegionCode() {
        DeviceRegistrationRequest request = createRequest("Bad-Region", "Huawei",
                "east-1", "S5", "10.0.0.5");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldValidateBatch() {
        List<DeviceRegistrationRequest> requests = List.of(
                createRequest("Device-1", "Huawei", "EAST", "S1", "10.0.0.1"),
                createRequest("Device-2", "ZTE", "WEST", "S2", "10.0.0.2"),
                createRequest("Device-3", "Cisco", "EAST", "S3", "10.0.0.3")
        );

        List<DeviceRegistrationRequest> valid = validator.validateBatch(requests);
        assertEquals(3, valid.size());
    }

    @Test
    void shouldFilterInvalidInBatch() {
        List<DeviceRegistrationRequest> requests = List.of(
                createRequest("Valid-1", "Huawei", "EAST", "S1", "10.0.0.1"),
                createRequest("", "ZTE", "WEST", "S2", "10.0.0.2"),
                createRequest("Valid-2", "Cisco", "EAST", "S3", "10.0.0.3")
        );

        List<DeviceRegistrationRequest> valid = validator.validateBatch(requests);
        assertEquals(2, valid.size());
    }

    @Test
    void shouldValidateCsvRow() {
        String[] validRow = {"Device-CSV-1", "Huawei", "EAST", "SITE-CSV", "10.0.0.100"};

        DeviceRegistrationRequest request = validator.validateCsvRow(validRow);
        assertNotNull(request);
        assertEquals("Device-CSV-1", request.getDeviceName());
        assertEquals("Huawei", request.getVendor());
    }

    @Test
    void shouldRejectInvalidCsvRow() {
        String[] invalidRow = {"Invalid@CSV!", "Huawei", "EAST", "S1", "10.0.0.1"};

        assertThrows(ValidationException.class, () -> validator.validateCsvRow(invalidRow));
    }

    @Test
    void shouldValidateJsonDevice() {
        String json = "{\"deviceName\":\"JSON-Device-1\",\"vendor\":\"Huawei\"," +
                "\"regionCode\":\"EAST\",\"siteCode\":\"S-JSON\",\"managementIp\":\"10.0.0.200\"}";

        DeviceRegistrationRequest request = validator.validateJsonDevice(json);
        assertNotNull(request);
        assertEquals("JSON-Device-1", request.getDeviceName());
    }

    @Test
    void shouldRejectInvalidJson() {
        assertThrows(ValidationException.class, () -> validator.validateJsonDevice("not-json"));
    }

    @Test
    void shouldDetectDuplicates() {
        DeviceRegistrationRequest r1 = createRequest("Duplicate", "Huawei", "EAST", "S1", "10.0.0.1");
        DeviceRegistrationRequest r2 = createRequest("Duplicate", "ZTE", "WEST", "S2", "10.0.0.2");

        validator.validate(r1);
        assertThrows(ValidationException.class, () -> validator.validate(r2));
    }

    @Test
    void shouldResetValidator() {
        validator.validate(createRequest("Reset-Test", "Huawei", "EAST", "S1", "10.0.0.1"));
        validator.reset();

        assertDoesNotThrow(() -> validator.validate(
                createRequest("Reset-Test", "Huawei", "EAST", "S1", "10.0.0.1")));
    }

    private DeviceRegistrationRequest createRequest(String name, String vendor,
                                                     String regionCode, String siteCode, String ip) {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceName(name);
        request.setVendor(vendor);
        request.setRegionCode(regionCode);
        request.setSiteCode(siteCode);
        request.setManagementIp(ip);
        return request;
    }
}
