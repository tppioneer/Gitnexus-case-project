package com.example.telecom.collector.validator;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.common.exception.ValidationException;

/**
 * Validates device registration requests before creating DeviceInfo.
 */
public class DeviceRegistrationValidator {

    private static final int MAX_DEVICE_NAME_LENGTH = 128;
    private static final int MAX_IP_LENGTH = 45;

    public void validate(DeviceRegistrationRequest request) {
        if (request.getDeviceName() == null || request.getDeviceName().isBlank()) {
            throw new ValidationException("deviceName", "Device name is required");
        }
        if (request.getDeviceName().length() > MAX_DEVICE_NAME_LENGTH) {
            throw new ValidationException("deviceName",
                    "Device name exceeds maximum length of " + MAX_DEVICE_NAME_LENGTH);
        }
        if (request.getDeviceType() == null) {
            throw new ValidationException("deviceType", "Device type is required");
        }
        if (request.getVendor() == null || request.getVendor().isBlank()) {
            throw new ValidationException("vendor", "Vendor is required");
        }
        if (request.getManagementIp() != null && request.getManagementIp().length() > MAX_IP_LENGTH) {
            throw new ValidationException("managementIp", "IP address exceeds maximum length");
        }
        if (request.getMaintenanceRegionCode() != null && request.getMaintenanceRegionCode().length() > 32) {
            throw new ValidationException("maintenanceRegionCode", "Maintenance region code exceeds maximum length");
        }
    }
}
