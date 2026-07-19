package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeCommand;
import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.DispatchResult;
import com.example.telecom.change.domain.EmergencyChangeCommand;
import com.example.telecom.change.domain.ChangeRisk;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Case N: Overload, Lambda and Method Reference Test.
 * Verifies overload resolution, lambda binding, method reference binding,
 * and callback registration/invocation.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class CaseNOverloadCallbackTest {

    @Autowired
    private ChangeCommandBus commandBus;

    @Autowired
    private ChangeCallbackRegistry callbackRegistry;

    @Autowired
    private ChangeExecutionService executionService;

    @Test
    void dispatch_changeCommand_resolvesToBaseOverload() {
        ChangeCommand cmd = new ChangeCommand("CHG-N-001", ChangeMode.LIVE, "base command");
        DispatchResult result = commandBus.dispatch((ChangeCommand) cmd);
        assertEquals("ChangeCommand", result.getDispatchedTo(),
                "dispatch(ChangeCommand) must resolve to base overload");
    }

    @Test
    void dispatch_emergencyCommand_resolvesToEmergencyOverload() {
        EmergencyChangeCommand cmd = new EmergencyChangeCommand(
                "CHG-N-002", ChangeMode.LIVE, "emergency", ChangeRisk.CRITICAL, "ops@example.com");
        DispatchResult result = commandBus.dispatch(cmd);
        assertEquals("EmergencyChangeCommand", result.getDispatchedTo(),
                "dispatch(EmergencyChangeCommand) must resolve to emergency overload");
    }

    @Test
    void dispatch_changeIdAndMode_resolvesToThirdOverload() {
        DispatchResult result = commandBus.dispatch("CHG-N-003", ChangeMode.DRY_RUN);
        assertEquals("ChangeId+Mode", result.getDispatchedTo(),
                "dispatch(String, ChangeMode) must resolve to third overload");
    }

    @Test
    void lambda_forEach_validatorInvocation() {
        // Validates that lambda forEach iterates and invokes validate on each
        var validators = List.of(
                new com.example.telecom.change.validation.RegionChangeValidator(),
                new com.example.telecom.change.validation.RiskChangeValidator()
        );
        var ctx = new com.example.telecom.change.domain.ChangeContext("CHG-N-004",
                com.example.telecom.change.domain.DeviceFamily.ROUTER, ChangeMode.LIVE,
                "east", "system", java.time.Instant.now());

        // Lambda forEach must invoke validate on each validator
        validators.forEach(v -> assertTrue(v.validate(ctx),
                v.validatorName() + " should validate successfully"));
    }

    @Test
    void methodReference_stepExecution() {
        // Stream + method reference: stepExecutor::executeStep
        List<com.example.telecom.change.domain.ExecutionResult> results =
                executionService.executeSteps(List.of("step1", "step2", "step3"));
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(r -> r.isSuccess()));
    }

    @Test
    void callbackRegistry_registerAndFire() {
        int initialCount = callbackRegistry.callbackCount(
                com.example.telecom.change.domain.ChangeStatus.COMPLETED);
        // @PostConstruct already registers auditService::onCompleted, so initialCount >= 1

        callbackRegistry.register(com.example.telecom.change.domain.ChangeStatus.COMPLETED,
                planId -> { /* additional callback */ });
        assertEquals(initialCount + 1, callbackRegistry.callbackCount(
                com.example.telecom.change.domain.ChangeStatus.COMPLETED));

        // Fire must invoke all registered callbacks without error and return the count fired
        int fired = callbackRegistry.fire(
                com.example.telecom.change.domain.ChangeStatus.COMPLETED, "CHG-N-005");
        assertEquals(initialCount + 1, fired);
    }

    @Test
    void callbackRegistry_methodReference_binding() {
        int initialCount = callbackRegistry.callbackCount(
                com.example.telecom.change.domain.ChangeStatus.COMPLETED);

        // Register a method reference callback on top of the existing @PostConstruct registrations
        com.example.telecom.change.service.ChangeAuditService auditService =
                new com.example.telecom.change.service.ChangeAuditService(null) {
                    @Override
                    public void onCompleted(String planId) {
                        // override to avoid JPA in this unit test
                    }
                };
        callbackRegistry.register(com.example.telecom.change.domain.ChangeStatus.COMPLETED,
                auditService::onCompleted);

        assertTrue(callbackRegistry.callbackCount(
                com.example.telecom.change.domain.ChangeStatus.COMPLETED) > initialCount);
    }

    @Test
    void overload_resolution_isDistinct() {
        // Prove that all three overloads produce distinct results
        ChangeCommand base = new ChangeCommand("CHG-X", ChangeMode.LIVE, "base");
        EmergencyChangeCommand emergency = new EmergencyChangeCommand(
                "CHG-X", ChangeMode.LIVE, "emergency", ChangeRisk.HIGH, "ops");

        DispatchResult r1 = commandBus.dispatch(base);
        DispatchResult r2 = commandBus.dispatch(emergency);
        DispatchResult r3 = commandBus.dispatch("CHG-X", ChangeMode.DRY_RUN);

        assertNotEquals(r1.getDispatchedTo(), r2.getDispatchedTo());
        assertNotEquals(r2.getDispatchedTo(), r3.getDispatchedTo());
        assertNotEquals(r1.getDispatchedTo(), r3.getDispatchedTo());
    }
}
