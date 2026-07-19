package com.example.telecom.change.aop;

/**
 * Sink for audit events emitted by {@code AuditOperationAspect}.
 * Abstraction allows swapping between in-memory (test) and
 * persistent (production) implementations.
 */
public interface AuditSink {
    /** Records that an audited operation is about to execute. */
    void before(String action, String signature);

    /** Records that an audited operation has completed. */
    void after(String action, boolean success);

    /** Returns all recorded audit entries (used in tests). */
    java.util.List<AuditEntry> entries();

    /** Simple audit log record. */
    record AuditEntry(String action, String signature, String phase, boolean success) {}
}
