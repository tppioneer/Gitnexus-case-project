package com.example.telecom.change.service;

import com.example.telecom.change.dto.ChangeRecordDto;
import com.example.telecom.change.controller.ChangeRoutes;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test: verifies noise classes exist but are NOT
 * counted as real entities, controllers, or routes.
 */
class NoiseClassTest {

    @Test
    void changeRecordDto_isNotAnEntity() {
        // ChangeRecordDto has a tableName field but is NOT annotated with @Entity
        ChangeRecordDto dto = new ChangeRecordDto(1L, "network_change", "CHG-X", "create", LocalDateTime.now());
        assertNotNull(dto.getTableName());
        assertFalse(dto.getClass().isAnnotationPresent(jakarta.persistence.Entity.class),
                "ChangeRecordDto must NOT be a JPA entity");
    }

    @Test
    void fakeTransactionalMarker_isNotSpringAnnotation() {
        FakeTransactionalMarker marker = new FakeTransactionalMarker();
        assertEquals("@Transactional", marker.getMarkerName());
        // The string "@Transactional" here is just a String — not a real annotation
        assertFalse(marker.getClass().isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));
    }

    @Test
    void changeDocumentation_isNotAController() {
        ChangeDocumentation doc = new ChangeDocumentation();
        // Contains route-like strings but has NO Spring MVC annotations
        assertFalse(doc.getClass().isAnnotationPresent(
                org.springframework.web.bind.annotation.RestController.class));
        assertTrue(doc.getMappingExamples().size() >= 3);
    }

    @Test
    void changeDocumentation_containsFakeRoute() {
        ChangeDocumentation doc = new ChangeDocumentation();
        String fakeRoute = doc.getFakeRouteLogMessage();
        assertTrue(fakeRoute.contains("/api/network-changes/fake"));
        // This string must NOT be counted as a real route
    }

    @Test
    void changeRoutes_constantsExist() {
        assertEquals("/pending", ChangeRoutes.PENDING);
        assertEquals("/draft", ChangeRoutes.DRAFT);
        assertEquals("/archived", ChangeRoutes.ARCHIVED);
        // DRAFT and ARCHIVED are noise constants — not real routes
    }

    @Test
    void unrelatedExecuteService_doesNotImplementChangeExecutor() {
        UnrelatedExecuteService service = new UnrelatedExecuteService();
        assertFalse(service instanceof com.example.telecom.change.executor.ChangeExecutor,
                "UnrelatedExecuteService must NOT implement ChangeExecutor");
    }

    @Test
    void changeRoutes_documentation_describeIsNotARoute() {
        ChangeDocumentation doc = new ChangeDocumentation();
        String description = doc.describe("/active");
        assertTrue(description.contains("/active"));
        // "/active" here is a parameter string — not a route annotation
    }
}
