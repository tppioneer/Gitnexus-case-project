package com.example.telecom.gateway.validator;

import com.example.telecom.common.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DashboardQueryValidator {

    private static final Set<String> VALID_FORMATS = new HashSet<>(Arrays.asList("PDF", "CSV", "EXCEL", "JSON"));
    private static final Set<String> VALID_AGGREGATIONS = new HashSet<>(Arrays.asList("HOURLY", "DAILY", "WEEKLY", "MONTHLY"));
    private static final int MAX_REGION_CODE_LENGTH = 32;
    private static final int MAX_PAGE_SIZE = 1000;
    private static final int MAX_RANGE_DAYS = 365;

    public void validateRegionCode(String regionCode) {
        if (regionCode == null || regionCode.isBlank()) {
            throw new ValidationException("regionCode", "Region code is required");
        }
        if (regionCode.length() > MAX_REGION_CODE_LENGTH) {
            throw new ValidationException("regionCode", "Region code must not exceed " + MAX_REGION_CODE_LENGTH + " characters");
        }
        if (!regionCode.matches("^[A-Za-z0-9_-]+$")) {
            throw new ValidationException("regionCode", "Region code must contain only letters, digits, hyphens and underscores");
        }
    }

    public void validateTimeRange(LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            throw new ValidationException("from", "Start time is required");
        }
        if (to == null) {
            throw new ValidationException("to", "End time is required");
        }
        if (from.isAfter(to)) {
            throw new ValidationException("timeRange", "Start time must be before end time");
        }
        if (from.plusDays(MAX_RANGE_DAYS).isBefore(to)) {
            throw new ValidationException("timeRange", "Time range must not exceed " + MAX_RANGE_DAYS + " days");
        }
        if (to.isAfter(LocalDateTime.now().plusDays(1))) {
            throw new ValidationException("timeRange", "End time cannot be in the future");
        }
    }

    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ValidationException("page", "Page must be >= 0");
        }
        if (size < 1) {
            throw new ValidationException("size", "Page size must be >= 1");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new ValidationException("size", "Page size must not exceed " + MAX_PAGE_SIZE);
        }
    }

    public void validateExportFormat(String format) {
        if (format == null || format.isBlank()) {
            throw new ValidationException("format", "Export format is required");
        }
        if (!VALID_FORMATS.contains(format.toUpperCase())) {
            throw new ValidationException("format",
                    "Invalid export format. Supported formats: " + String.join(", ", VALID_FORMATS));
        }
    }

    public void validateAggregationType(String type) {
        if (type == null || type.isBlank()) {
            throw new ValidationException("aggregationType", "Aggregation type is required");
        }
        if (!VALID_AGGREGATIONS.contains(type.toUpperCase())) {
            throw new ValidationException("aggregationType",
                    "Invalid aggregation type. Supported types: " + String.join(", ", VALID_AGGREGATIONS));
        }
    }
}
