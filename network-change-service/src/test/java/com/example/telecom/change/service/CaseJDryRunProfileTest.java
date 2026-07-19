package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.ExecutionResult;
import com.example.telecom.change.executor.ChangeExecutor;
import com.example.telecom.change.executor.ChangeExecutorRegistry;
import com.example.telecom.change.executor.DryRunChangeExecutor;
import com.example.telecom.change.executor.RemoteRollbackHandler;
import com.example.telecom.change.executor.RollbackHandler;
import com.example.telecom.change.executor.RouterChangeExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * dry-run profile variant — verifies that the dry-run executor,
 * remote rollback handler, and rollback resolution are deterministic
 * under this profile.
 */
@SpringBootTest
@ActiveProfiles({"benchmark", "dry-run"})
@TestPropertySource(properties = "telecom.change.remote.enabled=true")
class CaseJDryRunProfileTest {

    @Autowired private ApplicationContext context;
    @Autowired private ChangeExecutionService executionService;
    @Autowired private ChangeExecutorRegistry registry;

    @Test
    void dryRunExecutorIsActive() {
        assertTrue(context.containsBean("dryRunChangeExecutor"));
    }

    @Test
    void remoteRollbackHandlerIsActive() {
        assertTrue(context.containsBean("remoteRollbackHandler"));
    }

    @Test
    void remoteRollbackHandler_isActualBean() {
        RollbackHandler handler = executionService.getRollbackHandler();
        assertNotNull(handler, "getRollbackHandler must return a handler");
        assertInstanceOf(RemoteRollbackHandler.class, handler,
                "Under remote=true, RemoteRollbackHandler must be the single resolved bean");
    }

    @Test
    void standardRollbackHandler_notPresent() {
        assertFalse(context.containsBean("standardRollbackHandler"),
                "StandardRollbackHandler must be suppressed under remote=true");
    }

    @Test
    void perFamilyDispatch_stillUsesSpecificExecutors_inDryRunProfile() {
        // DryRunChangeExecutor registers as DeviceFamily.ALL, but the per-family
        // executors still resolve for ROUTER/TRANSMISSION/RADIO. ALL maps to DryRun.
        ChangeContext routerCtx = new ChangeContext("CHG-DR-001", DeviceFamily.ROUTER);
        ChangeContext transmissionCtx = new ChangeContext("CHG-DR-002", DeviceFamily.TRANSMISSION);
        ChangeContext radioCtx = new ChangeContext("CHG-DR-003", DeviceFamily.RADIO);

        assertEquals(RouterChangeExecutor.class,
                executionService.resolveExecutorClass(routerCtx));
        assertEquals(com.example.telecom.change.executor.TransmissionChangeExecutor.class,
                executionService.resolveExecutorClass(transmissionCtx));
        assertEquals(com.example.telecom.change.executor.RadioChangeExecutor.class,
                executionService.resolveExecutorClass(radioCtx));
    }

    @Test
    void dryRunExecutor_executesAndReturnsSuccess() {
        // Directly invoke the DryRun executor
        DryRunChangeExecutor dryRun = context.getBean(DryRunChangeExecutor.class);
        ChangeContext ctx = new ChangeContext("CHG-DR-004", DeviceFamily.ALL);
        ExecutionResult result = dryRun.execute(ctx);
        assertTrue(result.isSuccess());
        assertEquals("DryRunChangeExecutor", result.getExecutorName());
    }

    @Test
    void remoteRollbackHandler_executesWithRemoteMarker() {
        RollbackHandler handler = executionService.getRollbackHandler();
        ChangeContext ctx = new ChangeContext("CHG-DR-005", DeviceFamily.ROUTER, ChangeMode.LIVE,
                "east", "system", java.time.Instant.now());
        var result = handler.rollback(ctx);
        assertTrue(result.isApplied());
        assertTrue(result.getMessage().contains("[remote]"),
                "Remote handler must add [remote] marker to rollback message");
    }
}
