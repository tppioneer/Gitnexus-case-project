package com.example.telecom.change.annotation;

import com.example.telecom.change.domain.AuditCategory;
import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.service.ChangePlanService;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Case I: Java Annotation Binding Test.
 * Verifies that annotations are correctly defined, applied, and
 * their elements resolve to the right annotation methods.
 */
class CaseIAnnotationBindingTest {

    @Test
    void auditOperation_annotationTypeExists() {
        // Verify AuditOperation is a proper annotation type
        assertTrue(AuditOperation.class.isAnnotation());
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME,
                AuditOperation.class.getAnnotation(java.lang.annotation.Retention.class).value());
    }

    @Test
    void auditOperation_hasCorrectElements() throws NoSuchMethodException {
        // Verify the annotation has action(), category(), sensitive() methods
        assertNotNull(AuditOperation.class.getDeclaredMethod("action"));
        assertNotNull(AuditOperation.class.getDeclaredMethod("category"));
        assertNotNull(AuditOperation.class.getDeclaredMethod("sensitive"));
        // sensitive has default value false
        assertFalse(AuditOperation.class.getDeclaredMethod("sensitive").getDefaultValue().equals(true));
    }

    @Test
    void changePlanService_approveHas_auditOperationViaCriticalChange() throws Exception {
        // approve() is annotated with @CriticalChange which meta-annotates @AuditOperation
        Method approve = ChangePlanService.class.getMethod("approve", String.class, String.class);
        Annotation[] annotations = approve.getAnnotations();

        boolean hasCriticalChange = false;
        for (Annotation a : annotations) {
            if (a instanceof CriticalChange) {
                hasCriticalChange = true;
            }
        }
        assertTrue(hasCriticalChange, "approve() must have @CriticalChange");
    }

    @Test
    void criticalChange_isMetaAnnotatedWith_auditOperationAndChangeGuard() {
        // @CriticalChange must carry @AuditOperation and @ChangeGuard as meta-annotations
        AuditOperation auditOp = CriticalChange.class.getAnnotation(AuditOperation.class);
        assertNotNull(auditOp, "CriticalChange must be meta-annotated with @AuditOperation");
        assertEquals("critical-change", auditOp.action());
        assertEquals(AuditCategory.CHANGE, auditOp.category());
        assertTrue(auditOp.sensitive());

        ChangeGuard guard = CriticalChange.class.getAnnotation(ChangeGuard.class);
        assertNotNull(guard, "CriticalChange must be meta-annotated with @ChangeGuard");
        assertEquals(ChangeRisk.CRITICAL, guard.risk());
        assertTrue(guard.requireApproval());
    }

    @Test
    void changeGuard_appliedAtTypeLevel() {
        // @ChangeGuard on ChangePlanService class
        ChangeGuard guard = ChangePlanService.class.getAnnotation(ChangeGuard.class);
        assertNotNull(guard, "ChangePlanService must have @ChangeGuard at type level");
        assertEquals(ChangeRisk.LOW, guard.risk());
    }

    @Test
    void changePlanService_updateRisk_usesFullyQualifiedAnnotation() throws Exception {
        // updateRisk uses fully-qualified @com.example.telecom.change.annotation.AuditOperation
        Method updateRisk = ChangePlanService.class.getMethod("updateRisk", String.class, ChangeRisk.class);
        AuditOperation auditOp = updateRisk.getAnnotation(AuditOperation.class);
        assertNotNull(auditOp, "updateRisk must have @AuditOperation (FQN or short)");
        assertEquals("update-risk", auditOp.action());
        assertTrue(auditOp.sensitive(), "updateRisk must have sensitive=true");
    }

    @Test
    void opsReadEndpoint_isComposedRequestMappingGet() {
        // OpsReadEndpoint is a composed Spring mapping annotation.
        // Verify the meta-annotation chain, the @AliasFor binding, and
        // that the composed annotation alone produces a GET mapping.
        assertTrue(OpsReadEndpoint.class.isAnnotation());
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME,
                OpsReadEndpoint.class.getAnnotation(java.lang.annotation.Retention.class).value());

        // @RequestMapping(method = GET) must be present as a meta-annotation
        org.springframework.web.bind.annotation.RequestMapping metaMapping =
                OpsReadEndpoint.class.getAnnotation(
                        org.springframework.web.bind.annotation.RequestMapping.class);
        assertNotNull(metaMapping, "OpsReadEndpoint must carry @RequestMapping as meta-annotation");
        assertTrue(java.util.Arrays.asList(metaMapping.method())
                        .contains(org.springframework.web.bind.annotation.RequestMethod.GET),
                "OpsReadEndpoint meta @RequestMapping must include GET");

        // The path() element must have @AliasFor pointing at RequestMapping.path
        try {
            java.lang.reflect.Method pathMethod = OpsReadEndpoint.class.getDeclaredMethod("path");
            org.springframework.core.annotation.AliasFor aliasFor =
                    pathMethod.getAnnotation(org.springframework.core.annotation.AliasFor.class);
            assertNotNull(aliasFor, "OpsReadEndpoint.path() must carry @AliasFor");
            assertEquals(org.springframework.web.bind.annotation.RequestMapping.class,
                    aliasFor.annotation(), "@AliasFor must target RequestMapping");
            assertEquals("path", aliasFor.attribute(), "@AliasFor must bind to path");
        } catch (NoSuchMethodException e) {
            fail("OpsReadEndpoint must declare path()");
        }

        // ComposedEndpointController.getAuditTrail must be annotated with
        // @OpsReadEndpoint alone (no direct @GetMapping fallback).
        try {
            java.lang.reflect.Method handler =
                    com.example.telecom.change.controller.ComposedEndpointController.class
                            .getDeclaredMethod("getAuditTrail", String.class);
            assertNotNull(handler.getAnnotation(OpsReadEndpoint.class),
                    "getAuditTrail must carry @OpsReadEndpoint");
            assertNull(handler.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class),
                    "getAuditTrail must NOT carry a direct @GetMapping");
        } catch (NoSuchMethodException e) {
            fail("ComposedEndpointController must declare getAuditTrail(String)");
        }
    }

    @Test
    void changeExecutorCandidate_isMetaAnnotatedWithQualifier() {
        // @ChangeExecutorCandidate must carry @Qualifier as meta-annotation
        org.springframework.beans.factory.annotation.Qualifier qualifier =
                ChangeExecutorCandidate.class.getAnnotation(
                        org.springframework.beans.factory.annotation.Qualifier.class);
        assertNotNull(qualifier, "ChangeExecutorCandidate must be meta-annotated with @Qualifier");
    }
}
