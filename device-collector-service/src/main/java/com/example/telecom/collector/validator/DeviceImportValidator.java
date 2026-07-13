package com.example.telecom.collector.validator;

import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.common.exception.ValidationException;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DeviceImportValidator {

    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,128}$");
    private static final Pattern VENDOR_PATTERN = Pattern.compile("^[a-zA-Z0-9_ ]{1,64}$");
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\." +
            "(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    private static final Pattern REGION_CODE_PATTERN = Pattern.compile("^[A-Z]{2,8}$");
    private static final Pattern SITE_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,32}$");
    private static final Pattern CSV_DEVICE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_ -]{1,128}$");

    private final Set<String> seenDeviceNames = new HashSet<>();

    public void validate(DeviceRegistrationRequest request) {
        if (request.getDeviceName() == null || request.getDeviceName().isBlank()) {
            throw new ValidationException("deviceName", "Device name is required for import");
        }
        if (!DEVICE_NAME_PATTERN.matcher(request.getDeviceName()).matches()) {
            throw new ValidationException("deviceName",
                    "Device name must be alphanumeric with underscores/hyphens, max 128 chars");
        }
        if (seenDeviceNames.contains(request.getDeviceName())) {
            throw new ValidationException("deviceName",
                    "Duplicate device name in batch: " + request.getDeviceName());
        }
        if (request.getVendor() != null && !request.getVendor().isBlank()
                && !VENDOR_PATTERN.matcher(request.getVendor()).matches()) {
            throw new ValidationException("vendor", "Invalid vendor name format");
        }
        if (request.getManagementIp() != null && !request.getManagementIp().isBlank()
                && !IPV4_PATTERN.matcher(request.getManagementIp()).matches()) {
            throw new ValidationException("managementIp", "Invalid IPv4 address format");
        }
        if (request.getRegionCode() != null && !request.getRegionCode().isBlank()
                && !REGION_CODE_PATTERN.matcher(request.getRegionCode()).matches()) {
            throw new ValidationException("regionCode", "Region code must be 2-8 uppercase letters");
        }
        if (request.getSiteCode() != null && !request.getSiteCode().isBlank()
                && !SITE_CODE_PATTERN.matcher(request.getSiteCode()).matches()) {
            throw new ValidationException("siteCode", "Invalid site code format");
        }
        seenDeviceNames.add(request.getDeviceName());
    }

    public List<DeviceRegistrationRequest> validateBatch(List<DeviceRegistrationRequest> requests) {
        reset();
        List<DeviceRegistrationRequest> valid = new ArrayList<>();
        for (DeviceRegistrationRequest request : requests) {
            try {
                validate(request);
                valid.add(request);
            } catch (ValidationException e) {
                // Skip invalid records, continue with rest of batch
            }
        }
        return valid;
    }

    public DeviceRegistrationRequest validateCsvRow(String[] row) {
        if (row == null || row.length < 1) {
            throw new ValidationException("csvRow", "CSV row must have at least a device name");
        }
        String deviceName = row[0] != null ? row[0].trim() : "";
        if (!CSV_DEVICE_NAME_PATTERN.matcher(deviceName).matches()) {
            throw new ValidationException("deviceName", "Invalid device name in CSV: " + deviceName);
        }
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        request.setDeviceName(deviceName);
        if (row.length > 1 && row[1] != null) request.setVendor(row[1].trim());
        if (row.length > 2 && row[2] != null) request.setRegionCode(row[2].trim());
        if (row.length > 3 && row[3] != null) request.setSiteCode(row[3].trim());
        if (row.length > 4 && row[4] != null) {
            String ip = row[4].trim();
            if (!ip.isEmpty() && !IPV4_PATTERN.matcher(ip).matches()) {
                throw new ValidationException("managementIp", "Invalid IPv4 in CSV: " + ip);
            }
            request.setManagementIp(ip);
        }
        validate(request);
        return request;
    }

    public DeviceRegistrationRequest validateJsonDevice(String jsonDevice) {
        if (jsonDevice == null || jsonDevice.isBlank()) {
            throw new ValidationException("jsonDevice", "JSON device data is required");
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(jsonDevice);
            return validateJsonNode(node);
        } catch (Exception e) {
            throw new ValidationException("jsonDevice", "Failed to parse JSON device: " + e.getMessage());
        }
    }

    public DeviceRegistrationRequest validateJsonNode(com.fasterxml.jackson.databind.JsonNode node) {
        if (node == null) {
            throw new ValidationException("jsonNode", "JSON node must not be null");
        }
        DeviceRegistrationRequest request = new DeviceRegistrationRequest();
        if (node.has("deviceName")) {
            request.setDeviceName(node.get("deviceName").asText());
        } else {
            throw new ValidationException("deviceName", "deviceName is required in JSON");
        }
        if (node.has("vendor")) request.setVendor(node.get("vendor").asText());
        if (node.has("regionCode")) request.setRegionCode(node.get("regionCode").asText());
        if (node.has("siteCode")) request.setSiteCode(node.get("siteCode").asText());
        if (node.has("managementIp")) request.setManagementIp(node.get("managementIp").asText());
        validate(request);
        return request;
    }

    public void reset() {
        seenDeviceNames.clear();
    }

    private String validateField(String fieldName, String value, String pattern) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName, fieldName + " is required");
        }
        Pattern compiled = Pattern.compile(pattern);
        if (!compiled.matcher(value).matches()) {
            throw new ValidationException(fieldName,
                    fieldName + " does not match required pattern: " + pattern);
        }
        return value;
    }
}
