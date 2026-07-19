package com.example.telecom.change.service;

import org.springframework.stereotype.Service;

/**
 * External caller that invokes {@code ChangePlanService.recordInternal}
 * through a separate Spring-managed reference, ensuring proxy-based
 * transaction semantics apply.
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
