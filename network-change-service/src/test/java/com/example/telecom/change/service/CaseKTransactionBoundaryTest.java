package com.example.telecom.change.service;

import com.example.telecom.change.annotation.AuditOperation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Case K: Transaction Boundary Test.
 * Verifies @Transactional annotations and their properties are correctly
 * applied, and documents the self-invocation trap.
 */
class CaseKTransactionBoundaryTest {

    @Test
    void changePlanService_createPlan_hasTransactional() throws Exception {
        Method createPlan = ChangePlanService.class.getMethod("createPlan",
                com.example.telecom.change.domain.NetworkChangePlan.class);
        Transactional tx = createPlan.getAnnotation(Transactional.class);
        assertNotNull(tx, "createPlan must have @Transactional");
        assertFalse(tx.readOnly(), "createPlan must NOT be readOnly");
    }

    @Test
    void changePlanQueryService_findById_isReadOnly() throws Exception {
        Method findById = ChangePlanQueryService.class.getMethod("findById", String.class);
        Transactional tx = findById.getAnnotation(Transactional.class);
        assertNotNull(tx, "findById must have @Transactional");
        assertTrue(tx.readOnly(), "findById must be readOnly=true");
    }

    @Test
    void changeAuditService_record_requiresNew() throws Exception {
        Method record = ChangeAuditService.class.getMethod("record", String.class, String.class, String.class);
        Transactional tx = record.getAnnotation(Transactional.class);
        assertNotNull(tx, "record must have @Transactional");
        assertEquals(Propagation.REQUIRES_NEW, tx.propagation(),
                "record must have propagation=REQUIRES_NEW");
    }

    @Test
    void changeExecutionService_hasClassLevelTransactional() {
        Transactional tx = ChangeExecutionService.class.getAnnotation(Transactional.class);
        assertNotNull(tx, "ChangeExecutionService must have class-level @Transactional");
    }

    @Test
    void changePlanService_preview_overridesToReadOnly() throws Exception {
        // preview() overrides the class-level @Transactional to readOnly=true
        Method preview = ChangePlanService.class.getMethod("preview", String.class);
        Transactional tx = preview.getAnnotation(Transactional.class);
        assertNotNull(tx, "preview must override to readOnly=true");
        assertTrue(tx.readOnly(), "preview must be readOnly=true");
    }

    @Test
    void recordInternal_hasRequiresNew_but_selfInvocation_bypassesProxy() throws Exception {
        // recordInternal has REQUIRES_NEW, but when called from
        // approveAndRecordInternally via self-invocation, the proxy is bypassed.
        // This test documents the annotation exists but notes the runtime limitation.
        Method recordInternal = ChangePlanService.class.getMethod("recordInternal",
                String.class, String.class, String.class);
        Transactional tx = recordInternal.getAnnotation(Transactional.class);
        assertNotNull(tx, "recordInternal must have @Transactional");
        assertEquals(Propagation.REQUIRES_NEW, tx.propagation());

        // NOTE: Spring self-invocation (this.recordInternal()) does NOT go through
        // the proxy, so REQUIRES_NEW has NO effect when called from the same class.
        // This is a well-known Spring limitation — the annotation exists but
        // the proxy boundary is not crossed.
    }

    @Test
    void approve_hasCriticalChange_whichBringsAuditOperation() throws Exception {
        Method approve = ChangePlanService.class.getMethod("approve", String.class, String.class);
        assertNotNull(approve.getAnnotation(com.example.telecom.change.annotation.CriticalChange.class));
        // @CriticalChange is meta-annotated with @AuditOperation(action="critical-change")
        // This means the AOP aspect should intercept this call
    }
}
