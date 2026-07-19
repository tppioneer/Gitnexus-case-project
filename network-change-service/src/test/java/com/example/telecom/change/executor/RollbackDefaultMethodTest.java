package com.example.telecom.change.executor;

import com.example.telecom.change.domain.ChangeContext;
import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.domain.RollbackResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test: verifies RollbackHandler default method vs override behavior.
 */
class RollbackDefaultMethodTest {

    @Test
    void standardHandler_usesDefaultRollbackMethod() {
        StandardRollbackHandler handler = new StandardRollbackHandler();
        ChangeContext ctx = new ChangeContext("CHG-RB-001", DeviceFamily.ROUTER, ChangeMode.LIVE);

        // StandardRollbackHandler does NOT override rollback() — uses the default
        RollbackResult result = handler.rollback(ctx);
        assertTrue(result.isApplied());
        assertEquals("StandardRollbackHandler", result.getHandlerName());
    }

    @Test
    void remoteHandler_overridesRollbackMethod() {
        RemoteRollbackHandler handler = new RemoteRollbackHandler();
        ChangeContext ctx = new ChangeContext("CHG-RB-002", DeviceFamily.ROUTER, ChangeMode.LIVE,
                "east", "system", java.time.Instant.now());

        // RemoteRollbackHandler overrides rollback() to add "[remote]" marker
        RollbackResult result = handler.rollback(ctx);
        assertTrue(result.isApplied());
        assertTrue(result.getMessage().contains("[remote]"),
                "RemoteRollbackHandler.rollback must add [remote] marker");
    }

    @Test
    void standardHandler_invalidContext_returnsSkipped() {
        StandardRollbackHandler handler = new StandardRollbackHandler();
        ChangeContext ctx = new ChangeContext("", DeviceFamily.ROUTER, ChangeMode.LIVE);

        // validateRollback returns false for blank changeId → rollback skipped
        RollbackResult result = handler.rollback(ctx);
        assertFalse(result.isApplied());
    }

    @Test
    void remoteHandler_invalidContext_returnsSkipped() {
        RemoteRollbackHandler handler = new RemoteRollbackHandler();
        // remote handler requires regionCode — context with null regionCode fails validation
        ChangeContext ctx = new ChangeContext("CHG-VALID", DeviceFamily.ROUTER, ChangeMode.LIVE);
        // regionCode defaults to "default" — but remote needs explicit non-null.
        // Actually the default constructor sets regionCode="default", which is non-null.
        // Use a context where regionCode is explicitly null to trigger validation failure.
        ChangeContext ctxNoRegion = new ChangeContext("CHG-X", DeviceFamily.ROUTER, ChangeMode.LIVE,
                null, "system", java.time.Instant.now());
        RollbackResult result = handler.rollback(ctxNoRegion);
        assertFalse(result.isApplied(), "null regionCode should fail validation");
    }

    @Test
    void handlerNames_areDistinct() {
        StandardRollbackHandler standard = new StandardRollbackHandler();
        RemoteRollbackHandler remote = new RemoteRollbackHandler();

        assertNotEquals(standard.handlerName(), remote.handlerName());
        assertEquals("StandardRollbackHandler", standard.handlerName());
        assertEquals("RemoteRollbackHandler", remote.handlerName());
    }
}
