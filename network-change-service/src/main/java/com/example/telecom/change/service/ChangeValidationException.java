package com.example.telecom.change.service;

/**
 * Checked exception used to test {@code @Transactional(rollbackFor=...)}.
 * Thrown by {@code ChangePlanService.createPlan} when the plan is invalid.
 */
public class ChangeValidationException extends Exception {
    public ChangeValidationException(String message) {
        super(message);
    }
}
