package com.example.telecom.gateway.validator;

import com.example.telecom.common.exception.ValidationException;

public class DashboardQueryValidator {

    public void validateRegionCode(String regionCode) {
        if (regionCode == null || regionCode.isBlank()) {
            throw new ValidationException("regionCode", "Region code is required");
        }
        if (regionCode.length() > 32) {
            throw new ValidationException("regionCode", "Region code too long");
        }
    }

    public void validatePagination(int page, int pageSize) {
        if (page < 1) throw new ValidationException("page", "Page must be >= 1");
        if (pageSize < 1 || pageSize > 200) {
            throw new ValidationException("pageSize", "Page size must be between 1 and 200");
        }
    }
}
