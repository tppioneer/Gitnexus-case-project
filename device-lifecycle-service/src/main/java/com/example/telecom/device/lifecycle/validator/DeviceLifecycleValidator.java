package com.example.telecom.device.lifecycle.validator;

import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.device.lifecycle.dto.DeviceCertificateRequest;
import com.example.telecom.device.lifecycle.dto.DeviceFirmwareRequest;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class DeviceLifecycleValidator {

    private static final Pattern DEVICE_ID_PATTERN = Pattern.compile("^DEV-[A-Z0-9]{6,12}$");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");

    public void validate(DeviceLifecycleRequest request) {
        checkRequired(request.getDeviceId(), "deviceId");
        checkRequired(request.getDeviceName(), "deviceName");
        checkRequired(request.getDeviceType(), "deviceType");
        checkRequired(request.getVendor(), "vendor");
        checkRequired(request.getModel(), "model");
        checkRequired(request.getRegionCode(), "regionCode");
        validateDeviceId(request.getDeviceId());
    }

    public void validate(DeviceFirmwareRequest request) {
        checkRequired(request.getDeviceId(), "deviceId");
        checkRequired(request.getTargetVersion(), "targetVersion");
        validateDeviceId(request.getDeviceId());
        validateFirmwareVersion(request.getTargetVersion());
    }

    public void validate(DeviceCertificateRequest request) {
        checkRequired(request.getDeviceId(), "deviceId");
        checkRequired(request.getCertificateType(), "certificateType");
        validateDeviceId(request.getDeviceId());
        if (request.getValidityDays() < 1) {
            throw new ValidationException("validityDays", "Validity days must be positive");
        }
        if (request.getValidityDays() > 3650) {
            throw new ValidationException("validityDays", "Validity days cannot exceed 3650");
        }
    }

    public void validateDeviceId(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new ValidationException("deviceId", "Device ID is required");
        }
        if (!DEVICE_ID_PATTERN.matcher(deviceId).matches()) {
            throw new ValidationException("deviceId",
                    "Device ID must match pattern DEV-XXXXXXXX (DEV- followed by 6-12 alphanumeric characters)");
        }
    }

    public void validateFirmwareVersion(String version) {
        if (version == null || version.trim().isEmpty()) {
            throw new ValidationException("targetVersion", "Firmware version is required");
        }
        if (!VERSION_PATTERN.matcher(version).matches()) {
            throw new ValidationException("targetVersion",
                    "Firmware version must follow semantic versioning (e.g., 2.1.0)");
        }
    }

    public void validateIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return;
        }
        String ipPattern = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
        if (!java.util.regex.Pattern.matches(ipPattern, ipAddress)) {
            throw new ValidationException("ipAddress", "Invalid IP address format: " + ipAddress);
        }
    }

    public void validateRegionCode(String regionCode) {
        if (regionCode == null || regionCode.trim().isEmpty()) {
            throw new ValidationException("regionCode", "Region code is required");
        }
        if (!regionCode.matches("^[A-Z]{2}-[A-Z]+$")) {
            throw new ValidationException("regionCode",
                    "Region code must match pattern XX-REGION (e.g., US-EAST)");
        }
    }

    private void checkRequired(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, fieldName + " is required");
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " must not be empty");
        }
    }
}
