package com.example.telecom.collector.validator;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.common.exception.ValidationException;

import java.util.*;

public class DeviceImportValidator {

    private final Set<String> seenDeviceNames = new HashSet<>();

    public void validate(DeviceRegistrationRequest request) {
        if (request.getDeviceName() == null || request.getDeviceName().isBlank()) {
            throw new ValidationException("deviceName", "Device name is required for import");
        }
        if (seenDeviceNames.contains(request.getDeviceName())) {
            throw new ValidationException("deviceName",
                    "Duplicate device name in batch: " + request.getDeviceName());
        }
        seenDeviceNames.add(request.getDeviceName());
    }

    public void reset() {
        seenDeviceNames.clear();
    }
}
