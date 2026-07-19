package com.example.telecom.change.aop;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory {@code AuditSink} implementation — used for deterministic testing.
 * Records every before/after event in a list that tests can inspect.
 */
@Component
public class InMemoryAuditSink implements AuditSink {
    private final List<AuditEntry> entries = new ArrayList<>();

    @Override
    public synchronized void before(String action, String signature) {
        entries.add(new AuditEntry(action, signature, "before", true));
    }

    @Override
    public synchronized void after(String action, boolean success) {
        entries.add(new AuditEntry(action, "", "after", success));
    }

    @Override
    public synchronized List<AuditEntry> entries() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

    /** Clear all recorded entries. */
    public synchronized void clear() {
        entries.clear();
    }
}
