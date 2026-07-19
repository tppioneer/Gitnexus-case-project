package com.example.telecom.change.annotation;

import com.example.telecom.change.service.ChangePlanService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test for repeatable annotations.
 */
class CaseIRepeatableAnnotationTest {

    @Test
    void changePlanService_hasRepeatableRequiredCapability() {
        // ChangePlanService has @RequiredCapability("change.write") and @RequiredCapability("audit.log")
        RequiredCapability[] capabilities =
                ChangePlanService.class.getAnnotationsByType(RequiredCapability.class);
        assertEquals(2, capabilities.length, "ChangePlanService must have 2 @RequiredCapability");

        boolean hasWrite = false;
        boolean hasAudit = false;
        for (RequiredCapability cap : capabilities) {
            if ("change.write".equals(cap.value())) hasWrite = true;
            if ("audit.log".equals(cap.value())) hasAudit = true;
        }
        assertTrue(hasWrite, "Must have @RequiredCapability(\"change.write\")");
        assertTrue(hasAudit, "Must have @RequiredCapability(\"audit.log\")");
    }

    @Test
    void changePlanService_hasContainerAnnotation() {
        // The compiler generates @RequiredCapabilities container automatically
        RequiredCapabilities container =
                ChangePlanService.class.getAnnotation(RequiredCapabilities.class);
        assertNotNull(container, "Container annotation must be present");
        assertEquals(2, container.value().length);
    }
}
