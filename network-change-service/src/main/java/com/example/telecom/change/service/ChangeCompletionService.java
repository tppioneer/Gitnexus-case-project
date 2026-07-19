package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates post-completion activities when a change reaches a terminal
 * state. Fires registered callbacks through the {@code ChangeCallbackRegistry}
 * and records an audit entry for the completion event.
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
     * Completes a change plan by firing registered callbacks and recording
     * a completion audit entry.
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
