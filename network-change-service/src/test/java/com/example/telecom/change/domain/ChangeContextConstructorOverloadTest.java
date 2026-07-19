package com.example.telecom.change.domain;

import com.example.telecom.change.dto.ChangeRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test: verifies constructor overload resolution for ChangeContext.
 * Documents three distinct constructors with different parameter lists.
 */
class ChangeContextConstructorOverloadTest {

    @Test
    void twoArgConstructor_setsDefaults() {
        ChangeContext ctx = new ChangeContext("CHG-001", DeviceFamily.ROUTER);
        assertEquals("CHG-001", ctx.getChangeId());
        assertEquals(DeviceFamily.ROUTER, ctx.getDeviceFamily());
        assertEquals(ChangeMode.LIVE, ctx.getMode(), "2-arg constructor must default to LIVE mode");
    }

    @Test
    void threeArgConstructor_setsMode() {
        ChangeContext ctx = new ChangeContext("CHG-002", DeviceFamily.TRANSMISSION, ChangeMode.DRY_RUN);
        assertEquals(ChangeMode.DRY_RUN, ctx.getMode());
    }

    @Test
    void fullConstructor_setsAllFields() {
        Instant now = Instant.now();
        ChangeContext ctx = new ChangeContext("CHG-003", DeviceFamily.RADIO, ChangeMode.SAFE,
                "east", "operator-1", now);
        assertEquals("east", ctx.getRegionCode());
        assertEquals("operator-1", ctx.getOperatorId());
        assertEquals(now, ctx.getRequestedAt());
    }

    @Test
    void requestConstructor_buildsFromDto() {
        // ChangeContext(ChangeRequest) constructor extracts fields from the DTO
        ChangeRequest req = new ChangeRequest("Test Plan", "east",
                com.example.telecom.change.domain.DeviceFamily.ROUTER,
                com.example.telecom.change.domain.ChangeRisk.LOW,
                com.example.telecom.change.domain.ChangeMode.LIVE,
                "description");
        ChangeContext ctx = new ChangeContext(req);
        assertEquals(com.example.telecom.change.domain.DeviceFamily.ROUTER, ctx.getDeviceFamily());
        assertEquals(com.example.telecom.change.domain.ChangeMode.LIVE, ctx.getMode());
        assertEquals("east", ctx.getRegionCode());
    }

    @Test
    void dispatchResult_accepted() {
        DispatchResult result = DispatchResult.accepted("target", "accepted");
        assertTrue(result.isAccepted());
        assertEquals("target", result.getDispatchedTo());
    }

    @Test
    void dispatchResult_rejected() {
        DispatchResult result = DispatchResult.rejected("target", "rejected");
        assertFalse(result.isAccepted());
    }

    @Test
    void executionResult_success_and_failure() {
        ExecutionResult success = ExecutionResult.success("executor", "ok");
        assertTrue(success.isSuccess());
        assertEquals("executor", success.getExecutorName());

        ExecutionResult failure = ExecutionResult.failure("executor", "error");
        assertFalse(failure.isSuccess());
    }

    @Test
    void rollbackResult_applied_and_skipped() {
        RollbackResult applied = RollbackResult.applied("handler", "rolled back");
        assertTrue(applied.isApplied());
        assertEquals("handler", applied.getHandlerName());

        RollbackResult skipped = RollbackResult.skipped();
        assertFalse(skipped.isApplied());
    }
}
