package com.example.telecom.common.exception;

public class ValidationException extends DomainException {
    private final String fieldName;

    public ValidationException(String fieldName, String message) {
        super("VALIDATION_ERROR", message);
        this.fieldName = fieldName;
    }

    public String getFieldName() { return fieldName; }
}
