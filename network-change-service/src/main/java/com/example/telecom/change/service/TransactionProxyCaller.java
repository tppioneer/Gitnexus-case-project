package com.example.telecom.change.service;

import org.springframework.stereotype.Service;

/**
 * External caller used to exercise proxy-based invocation of
 * {@code ChangePlanService.recordInternal}. Because this bean is a separate
 * Spring-managed object, a call to {@code planService.recordInternal(...)}
 * goes through the proxy, so {@code REQUIRES_NEW} semantics apply.
 *
 * This is the counterpart to the self-invocation scenario inside
 * {@code ChangePlanService} itself, where the proxy is bypassed.
 */
@Service
public class TransactionProxyCaller {

    private final ChangePlanService planService;

    public TransactionProxyCaller(ChangePlanService planService) {
        this.planService = planService;
    }

    public void callRecordInternalExternally(String planId, String action, String details) {
        planService.recordInternal(planId, action, details);
    }
}
