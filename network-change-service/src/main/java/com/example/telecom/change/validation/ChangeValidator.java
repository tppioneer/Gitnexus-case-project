package com.example.telecom.change.validation;

import com.example.telecom.change.domain.ChangeContext;

/**
 * Interface for change validators. Multiple implementations are
 * registered and invoked in sequence.
 */
public interface ChangeValidator {
    boolean validate(ChangeContext context);
    String validatorName();
}
