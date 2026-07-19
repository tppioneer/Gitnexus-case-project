package com.example.telecom.change.validation;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ChangeRisk;
import org.springframework.stereotype.Component;

/**
 * Validates risk-level constraints.
 */
@Component
public class RiskChangeValidator implements ChangeValidator {

    @Override
    public boolean validate(ChangeContext context) {
        return context.getMode() != null;
    }

    @Override
    public String validatorName() {
        return "RiskChangeValidator";
    }

    /** Validate that risk is not CRITICAL without approval. */
    public boolean validateRiskLevel(ChangeRisk risk, boolean hasApproval) {
        if (risk == ChangeRisk.CRITICAL && !hasApproval) {
            return false;
        }
        return true;
    }
}
