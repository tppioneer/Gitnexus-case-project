package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates post-completion activities when a change reaches a terminal
 * state. Fires registered callbacks so that listeners (audit, notification,
 * etc.) are invoked through the {@code ChangeCallbackRegistry} mechanism,
 * and records an orchestration-level audit entry alongside the
 * callback-generated audit.
 */
@Service
public class ChangeCompletionService {

    private final ChangeCallbackRegistry callbackRegistry;
    private final ChangeAuditService auditService;

    public ChangeCompletionService(ChangeCallbackRegistry callbackRegistry,
                                   ChangeAuditService auditService) {
        this.callbackRegistry = callbackRegistry;
        this.auditService = auditService;
    }

    /**
     * Called when a change plan transitions to a terminal status.
     * Fires registered callbacks through the registry; the callbacks
     * (e.g. {@code auditService::onCompleted}) record their own audit
     * entries. This method also records its own orchestration audit
     * entry with a distinct action name so that callback execution is
     * unambiguously identifiable.
     *
     * @return the number of callbacks invoked
     */
    @Transactional
    public int completeChange(String planId, ChangeStatus terminalStatus) {
        int invoked = callbackRegistry.fire(terminalStatus, planId);
        auditService.record(planId, "completion-orchestrated",
                "Status=" + terminalStatus + ", callbacks=" + invoked);
        return invoked;
    }
}
