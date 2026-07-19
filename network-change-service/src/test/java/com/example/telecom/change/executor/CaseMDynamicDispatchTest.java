package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import com.example.telecom.change.service.ChangeExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Case M: Dynamic Dispatch Test.
 * Verifies that interface dispatch, template method, and default method
 * resolve to the correct runtime targets under fixed configuration.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class CaseMDynamicDispatchTest {

    @Autowired
    private ChangeExecutionService executionService;

    @Test
    void router_dispatch_resolvesToRouterExecutor() {
        ChangeContext ctx = new ChangeContext("CHG-M-001", DeviceFamily.ROUTER, ChangeMode.LIVE);
        Class<?> resolved = executionService.resolveExecutorClass(ctx);
        assertEquals(RouterChangeExecutor.class, resolved,
                "ROUTER family must resolve to RouterChangeExecutor");
    }

    @Test
    void transmission_dispatch_resolvesToTransmissionExecutor() {
        ChangeContext ctx = new ChangeContext("CHG-M-002", DeviceFamily.TRANSMISSION, ChangeMode.LIVE);
        Class<?> resolved = executionService.resolveExecutorClass(ctx);
        assertEquals(TransmissionChangeExecutor.class, resolved,
                "TRANSMISSION family must resolve to TransmissionChangeExecutor");
    }

    @Test
    void radio_dispatch_resolvesToRadioExecutor() {
        ChangeContext ctx = new ChangeContext("CHG-M-003", DeviceFamily.RADIO, ChangeMode.LIVE);
        Class<?> resolved = executionService.resolveExecutorClass(ctx);
        assertEquals(RadioChangeExecutor.class, resolved,
                "RADIO family must resolve to RadioChangeExecutor");
    }

    @Test
    void router_execute_returnsRouterResult() {
        ChangeContext ctx = new ChangeContext("CHG-M-004", DeviceFamily.ROUTER, ChangeMode.LIVE);
        ExecutionResult result = executionService.execute(ctx);
        assertTrue(result.isSuccess());
        assertEquals("RouterChangeExecutor", result.getExecutorName());
    }

    @Test
    void transmission_execute_returnsTransmissionResult() {
        ChangeContext ctx = new ChangeContext("CHG-M-005", DeviceFamily.TRANSMISSION, ChangeMode.LIVE);
        ExecutionResult result = executionService.execute(ctx);
        assertTrue(result.isSuccess());
        assertEquals("TransmissionChangeExecutor", result.getExecutorName());
    }

    @Test
    void radio_execute_returnsRadioResult() {
        ChangeContext ctx = new ChangeContext("CHG-M-006", DeviceFamily.RADIO, ChangeMode.LIVE);
        ExecutionResult result = executionService.execute(ctx);
        assertTrue(result.isSuccess());
        assertEquals("RadioChangeExecutor", result.getExecutorName());
    }

    @Test
    void safeExecute_alwaysResolvesToSafeChangeExecutor() {
        ChangeContext ctx = new ChangeContext("CHG-M-007", DeviceFamily.ROUTER, ChangeMode.SAFE);
        ExecutionResult result = executionService.executeSafe(ctx);
        assertTrue(result.isSuccess());
        assertEquals("SafeChangeExecutor", result.getExecutorName(),
                "safeExecutor.execute must always resolve to SafeChangeExecutor");
    }

    @Test
    void templateMethod_validateIsCalled() {
        // Invalid context should trigger validation failure
        ChangeContext ctx = new ChangeContext("", DeviceFamily.ROUTER, ChangeMode.LIVE);
        assertThrows(IllegalArgumentException.class, () -> executionService.execute(ctx),
                "Template method validate() must throw for blank changeId");
    }

    @Test
    void rollbackHandler_defaultMethod_usesStandardHandler() {
        ChangeContext ctx = new ChangeContext("CHG-M-008", DeviceFamily.ROUTER, ChangeMode.LIVE);
        RollbackHandler handler = executionService.getRollbackHandler();
        assertNotNull(handler);
        // StandardRollbackHandler inherits default rollback method
        var result = handler.rollback(ctx);
        assertTrue(result.isApplied());
    }
}
