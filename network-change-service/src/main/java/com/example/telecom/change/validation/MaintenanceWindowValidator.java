package com.example.telecom.change.validation;

import com.example.telecom.change.domain.ChangeContext;
import org.springframework.stereotype.Component;

/**
 * Validates that maintenance windows are respected.
 * The "validate" method name is noise — it's a different validate
 * from ChangeValidator.validate and ChangePlanService.validate.
 */
@Component
public class MaintenanceWindowValidator implements ChangeValidator {

    @Override
    public boolean validate(ChangeContext context) {
        // Simplified: always passes in benchmark mode
        return true;
    }

    @Override
    public String validatorName() {
        return "MaintenanceWindowValidator";
    }

    /** Validate with explicit window — noise method with same name "validate". */
    public boolean validate(String regionCode, String windowStart, String windowEnd) {
        return regionCode != null && windowStart != null && windowEnd != null;
    }
}
