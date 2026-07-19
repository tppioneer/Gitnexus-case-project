package com.example.telecom.change.aop;

import com.example.telecom.change.domain.NetworkChangePlan;
import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.service.ChangePlanService;
import com.example.telecom.change.service.ChangeValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AOP runtime oracle — calls @AuditOperation-annotated methods through
 * the Spring proxy and asserts that InMemoryAuditSink records before/after
 * events. Also verifies the @CriticalChange meta-annotation path.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class AuditOperationAspectRuntimeTest {

    @Autowired private ChangePlanService planService;
    @Autowired private InMemoryAuditSink auditSink;

    @Test
    void createPlan_isIntercepted_viaAuditOperation() throws Exception {
        auditSink.clear();
        NetworkChangePlan plan = new NetworkChangePlan("CHG-AOP-001", "AOP Test",
                "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        planService.createPlan(plan);

        var entries = auditSink.entries();
        assertTrue(entries.stream().anyMatch(e -> "before".equals(e.phase())
                && "create-plan".equals(e.action())),
                "before phase must record create-plan action");
        assertTrue(entries.stream().anyMatch(e -> "after".equals(e.phase())
                && e.success()),
                "after phase must record success");
    }

    @Test
    void updateRisk_isIntercepted_withSensitiveFlag() {
        auditSink.clear();
        // Create plan first so updateRisk finds it
        try {
            planService.createPlan(new NetworkChangePlan("CHG-AOP-002", "AOP Test 2",
                    "east", DeviceFamily.ROUTER, ChangeRisk.LOW));
        } catch (ChangeValidationException ignored) {}

        auditSink.clear();
        planService.updateRisk("CHG-AOP-002", ChangeRisk.HIGH);

        var entries = auditSink.entries();
        assertTrue(entries.stream().anyMatch(e -> "before".equals(e.phase())
                && "update-risk".equals(e.action())),
                "before phase must record update-risk action");
    }

    @Test
    void approve_interceptedViaCriticalChange_metaAnnotation() throws Exception {
        auditSink.clear();
        planService.createPlan(new NetworkChangePlan("CHG-AOP-003", "AOP Test 3",
                "east", DeviceFamily.ROUTER, ChangeRisk.LOW));

        auditSink.clear();
        planService.approve("CHG-AOP-003", "approver-1");

        // approve() is annotated with @CriticalChange which meta-carries
        // @AuditOperation(action = "critical-change", sensitive = true).
        var entries = auditSink.entries();
        assertTrue(entries.stream().anyMatch(e -> "before".equals(e.phase())
                && "critical-change".equals(e.action())),
                "CriticalChange must produce audit with action=critical-change");
    }

    @Test
    void exceptionBranch_recordsAfterWithSuccessFalse() {
        auditSink.clear();
        // createPlan with null planId throws ChangeValidationException
        NetworkChangePlan invalidPlan = new NetworkChangePlan(null, "Bad Plan",
                "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        try {
            planService.createPlan(invalidPlan);
            fail("Expected ChangeValidationException");
        } catch (ChangeValidationException expected) {
            // Expected
        }

        var entries = auditSink.entries();
        assertTrue(entries.stream().anyMatch(e -> "before".equals(e.phase())),
                "before phase must be recorded");
        assertTrue(entries.stream().anyMatch(e -> "after".equals(e.phase()) && !e.success()),
                "after phase must record success=false on exception");
    }
}
