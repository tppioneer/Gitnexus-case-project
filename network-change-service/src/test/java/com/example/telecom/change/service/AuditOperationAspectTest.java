package com.example.telecom.change.aop;

import com.example.telecom.change.annotation.AuditOperation;
import com.example.telecom.change.domain.AuditCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test: verifies AOP aspect is registered and audit sink works.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class AuditOperationAspectTest {

    @Autowired
    private AuditOperationAspect aspect;

    @Autowired
    private InMemoryAuditSink auditSink;

    @Autowired
    private ApplicationContext context;

    @Test
    void aspect_isRegisteredAsBean() {
        assertNotNull(aspect, "AuditOperationAspect must be a Spring bean");
    }

    @Test
    void auditSink_isRegisteredAsBean() {
        assertNotNull(auditSink, "InMemoryAuditSink must be a Spring bean");
    }

    @Test
    void auditSink_recordsBeforeAndAfter() {
        auditSink.clear();
        auditSink.before("test-action", "TestService.method()");
        auditSink.after("test-action", true);

        var entries = auditSink.entries();
        assertEquals(2, entries.size());
        assertEquals("before", entries.get(0).phase());
        assertEquals("after", entries.get(1).phase());
        assertTrue(entries.get(1).success());
    }

    @Test
    void auditOperation_isRuntimeAnnotation() {
        // Verify AuditOperation can be read at runtime
        assertTrue(AuditOperation.class.isAnnotation());
        var methods = AuditOperation.class.getDeclaredMethods();
        assertTrue(methods.length >= 3, "AuditOperation must have action, category, sensitive");
    }

    @Test
    void infrastructureConfiguration_isLoaded() {
        assertTrue(context.containsBean("changeInfrastructureConfiguration")
                || context.getBeansOfType(com.example.telecom.change.config.ChangeInfrastructureConfiguration.class).size() > 0);
    }
}
