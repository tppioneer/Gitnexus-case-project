package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeStatus;
import com.example.telecom.change.entity.ChangeAuditEntity;
import com.example.telecom.change.repository.ChangeAuditJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the production callback chain: ChangeCompletionService →
 * ChangeCallbackRegistry.fire() → auditService::onCompleted →
 * ChangeAuditService.record(action="callback-change-completed").
 *
 * Verifies that the @PostConstruct-registered method reference callback
 * is actually invoked through a production business entry point, and
 * that the test cannot pass through a direct audit write alone.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class ChangeCompletionServiceTest {

    @Autowired private ChangeCompletionService completionService;
    @Autowired private ChangeCallbackRegistry callbackRegistry;
    @Autowired private ChangeAuditJpaRepository auditRepo;

    @Test
    void completedCallback_isRegisteredAtStartup() {
        assertTrue(callbackRegistry.callbackCount(ChangeStatus.COMPLETED) >= 1,
                "auditService::onCompleted must be registered at startup");
    }

    @Test
    void completeChange_firesRegisteredCallbacks_writesCallbackAudit() {
        auditRepo.deleteAll();
        auditRepo.flush();

        String planId = "CHG-COMP-001";

        int invoked = completionService.completeChange(planId, ChangeStatus.COMPLETED);
        assertTrue(invoked >= 1, "at least one callback must be invoked");

        // The callback writes "callback-change-completed".
        // If the callback did NOT execute, this assertion would fail
        // (the direct orchestrator write uses "completion-orchestrated").
        List<ChangeAuditEntity> callbackRecords =
                auditRepo.findByAction("callback-change-completed");
        assertEquals(1, callbackRecords.size(),
                "exactly one callback-change-completed audit proves the callback executed");
        assertTrue(callbackRecords.get(0).getDetails().contains(planId),
                "callback audit must reference the planId");
    }

    @Test
    void completeChange_alsoWritesOrchestrationAudit() {
        auditRepo.deleteAll();
        auditRepo.flush();

        String planId = "CHG-COMP-002";
        completionService.completeChange(planId, ChangeStatus.COMPLETED);

        // The orchestrator write uses "completion-orchestrated" (distinct from callback action)
        List<ChangeAuditEntity> orchestratorRecords =
                auditRepo.findByAction("completion-orchestrated");
        assertEquals(1, orchestratorRecords.size(),
                "orchestrator must also record its own audit");
    }

    @Test
    void callbackAndOrchestratorWrites_areDistinct() {
        auditRepo.deleteAll();
        auditRepo.flush();

        String planId = "CHG-COMP-003";
        completionService.completeChange(planId, ChangeStatus.COMPLETED);

        // Prove that callback audit and orchestrator audit are separate records
        List<ChangeAuditEntity> allRecords = auditRepo.findAll();
        long callbackCount = allRecords.stream()
                .filter(r -> "callback-change-completed".equals(r.getAction())).count();
        long orchestratorCount = allRecords.stream()
                .filter(r -> "completion-orchestrated".equals(r.getAction())).count();

        assertEquals(1, callbackCount, "one callback audit record");
        assertEquals(1, orchestratorCount, "one orchestrator audit record");
    }

    @Test
    void completeChange_returnsCallbackCount() {
        int initial = callbackRegistry.callbackCount(ChangeStatus.COMPLETED);
        int invoked = completionService.completeChange("CHG-COMP-004", ChangeStatus.COMPLETED);
        assertEquals(initial, invoked,
                "invoked count must match the number of registered callbacks for COMPLETED");
    }

    @Test
    void callbackNotExecuted_wouldCauseTestFailure() {
        // Negative oracle: register a status with NO callbacks, then verify
        // that callback-change-completed audit does NOT appear.
        auditRepo.deleteAll();
        auditRepo.flush();

        // COMPLETED has callbacks → should produce callback audit
        completionService.completeChange("CHG-COMP-005", ChangeStatus.COMPLETED);
        assertEquals(1, auditRepo.findByAction("callback-change-completed").size());

        auditRepo.deleteAll();
        auditRepo.flush();

        // DRAFT has no callbacks → should NOT produce callback audit
        int draftInvoked = completionService.completeChange("CHG-COMP-006", ChangeStatus.DRAFT);
        assertEquals(0, draftInvoked, "DRAFT status has no registered callbacks");
        assertEquals(0, auditRepo.findByAction("callback-change-completed").size(),
                "no callback audit when no callbacks are registered — proves callback is the sole source");
    }
}
