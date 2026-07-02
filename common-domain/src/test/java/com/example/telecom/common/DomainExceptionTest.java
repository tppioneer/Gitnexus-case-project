package com.example.telecom.common;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    void shouldCreateDomainException() {
        DomainException ex = new DomainException("ERR_001", "Error message");
        assertEquals("ERR_001", ex.getErrorCode());
        assertEquals("Error message", ex.getMessage());
    }

    @Test
    void shouldCreateDomainExceptionWithCause() {
        RuntimeException cause = new RuntimeException("root cause");
        DomainException ex = new DomainException("ERR_002", "Wrapped error", cause);
        assertEquals("ERR_002", ex.getErrorCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void shouldCreateValidationException() {
        ValidationException ex = new ValidationException("email", "Invalid email format");
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
        assertEquals("email", ex.getFieldName());
        assertEquals("Invalid email format", ex.getMessage());
    }
}
