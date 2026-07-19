package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Registry for status-based callbacks. Demonstrates callback registration
 * and invocation patterns with method references and lambdas.
 */
@Component
public class ChangeCallbackRegistry {

    private final Map<ChangeStatus, List<Consumer<String>>> callbacks = new EnumMap<>(ChangeStatus.class);
    private final ChangeAuditService auditService;

    public ChangeCallbackRegistry(ChangeAuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * On startup, registers {@code auditService::onCompleted} as a method-reference
     * callback for the COMPLETED status.
     */
    @PostConstruct
    void init() {
        register(ChangeStatus.COMPLETED, auditService::onCompleted);
    }

    /** Register a callback for a specific status change. */
    public void register(ChangeStatus status, Consumer<String> callback) {
        callbacks.computeIfAbsent(status, k -> new ArrayList<>()).add(callback);
    }

    /**
     * Fire all callbacks registered for the given status. Returns the number
     * of callbacks invoked — callers can use this to verify actual execution.
     */
    public int fire(ChangeStatus status, String planId) {
        List<Consumer<String>> handlers = callbacks.getOrDefault(status, List.of());
        handlers.forEach(h -> h.accept(planId));
        return handlers.size();
    }

    /** Return the number of registered callbacks for a status. */
    public int callbackCount(ChangeStatus status) {
        return callbacks.getOrDefault(status, List.of()).size();
    }
}
