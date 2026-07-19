package com.example.telecom.change.validation;

import com.example.telecom.change.annotation.RegionScope;
import com.example.telecom.change.domain.ChangeContext;
import org.springframework.stereotype.Component;

/**
 * Validates that the change context has a valid region code.
 */
@Component
public class RegionChangeValidator implements ChangeValidator {

    @Override
    public boolean validate(ChangeContext context) {
        return context.getRegionCode() != null && !context.getRegionCode().isBlank();
    }

    @Override
    public String validatorName() {
        return "RegionChangeValidator";
    }

    /**
     * Demonstrates @RegionScope on a method — the annotation applies
     * to the method level, restricting its effective region scope.
     */
    @RegionScope(value = "east", includeChildren = true)
    public boolean validateForEast(ChangeContext context) {
        return "east".equals(context.getRegionCode());
    }
}
