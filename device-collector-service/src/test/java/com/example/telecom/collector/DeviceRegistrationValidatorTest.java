package com.example.telecom.collector;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.validator.DeviceRegistrationValidator;
import com.example.telecom.common.device.DeviceType;
import com.example.telecom.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceRegistrationValidatorTest {

    private DeviceRegistrationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DeviceRegistrationValidator();
    }

    @Test
    void shouldValidateCompleteRequest() {
        DeviceRegistrationRequest request = validRequest();
        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void shouldRejectMissingDeviceName() {
        DeviceRegistrationRequest request = validRequest();
        request.setDeviceName(null);
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectBlankDeviceName() {
        DeviceRegistrationRequest request = validRequest();
        request.setDeviceName("   ");
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectLongDeviceName() {
        DeviceRegistrationRequest request = validRequest();
        request.setDeviceName("A".repeat(129));
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectMissingDeviceType() {
        DeviceRegistrationRequest request = validRequest();
        request.setDeviceType(null);
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectMissingVendor() {
        DeviceRegistrationRequest request = validRequest();
        request.setVendor(null);
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectBlankVendor() {
        DeviceRegistrationRequest request = validRequest();
        request.setVendor("");
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectLongIpAddress() {
        DeviceRegistrationRequest request = validRequest();
        request.setManagementIp("A".repeat(46));
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldRejectLongRegionCode() {
        DeviceRegistrationRequest request = validRequest();
        request.setRegionCode("A".repeat(33));
        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    void shouldAcceptNullRegionCode() {
        DeviceRegistrationRequest request = validRequest();
        request.setRegionCode(null);
        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void shouldAcceptNullIp() {
        DeviceRegistrationRequest request = validRequest();
        request.setManagementIp(null);
        assertDoesNotThrow(() -> validator.validate(request));
    }

    private DeviceRegistrationRequest validRequest() {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceName("BS-East-01");
        request.setDeviceType(DeviceType.BASE_STATION);
        request.setVendor("Huawei");
        request.setRegionCode("EAST");
        request.setSiteCode("SITE-001");
        request.setManagementIp("10.0.0.1");
        return request;
    }
}
