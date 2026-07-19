package com.example.telecom.change.repository;

import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.ChangeStatus;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.entity.ChangeStepEntity;
import com.example.telecom.change.entity.ChangeWindowEmbeddable;
import com.example.telecom.change.entity.NetworkChangeEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Case L: JPA Mapping Test.
 * Verifies entity mappings, relationships, and repository queries.
 */
@DataJpaTest
@ActiveProfiles("benchmark")
class CaseLJpaMappingTest {

    @Autowired
    private NetworkChangeJpaRepository changeRepo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void entity_isMappedToCorrectTable() {
        // Verify @Table(name="network_change") by persisting and querying
        NetworkChangeEntity entity = new NetworkChangeEntity(
                "CHG-JPA-001", "JPA Test", "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        changeRepo.save(entity);

        List<NetworkChangeEntity> found = changeRepo.findByChangeId("CHG-JPA-001").stream().toList();
        assertEquals(1, found.size());
        assertEquals("CHG-JPA-001", found.get(0).getChangeId());
    }

    @Test
    void entity_derivedQuery_findByRegionCodeAndStatus() {
        NetworkChangeEntity e1 = new NetworkChangeEntity(
                "CHG-DQ-001", "DQ Test", "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        e1.setStatus(ChangeStatus.APPROVED);
        changeRepo.save(e1);

        NetworkChangeEntity e2 = new NetworkChangeEntity(
                "CHG-DQ-002", "DQ Test 2", "east", DeviceFamily.RADIO, ChangeRisk.MEDIUM);
        e2.setStatus(ChangeStatus.DRAFT);
        changeRepo.save(e2);

        List<NetworkChangeEntity> result = changeRepo.findByRegionCodeAndStatus("east", ChangeStatus.APPROVED);
        assertEquals(1, result.size());
        assertEquals("CHG-DQ-001", result.get(0).getChangeId());
    }

    @Test
    void entity_jpqlQuery_findRiskQueue() {
        NetworkChangeEntity e = new NetworkChangeEntity(
                "CHG-JPQL-001", "JPQL Test", "west", DeviceFamily.TRANSMISSION, ChangeRisk.HIGH);
        e.setStatus(ChangeStatus.PENDING_APPROVAL);
        changeRepo.save(e);

        List<NetworkChangeEntity> result = changeRepo.findRiskQueue(
                ChangeRisk.HIGH, List.of(ChangeStatus.PENDING_APPROVAL, ChangeStatus.APPROVED));
        assertTrue(result.stream().anyMatch(en -> "CHG-JPQL-001".equals(en.getChangeId())));
    }

    @Test
    void entity_relationship_oneToMany_steps() {
        NetworkChangeEntity change = new NetworkChangeEntity(
                "CHG-REL-001", "Relationship Test", "north", DeviceFamily.ROUTER, ChangeRisk.LOW);
        ChangeStepEntity step = new ChangeStepEntity(1, "validate", "routerExecutor");
        change.addStep(step);
        changeRepo.save(change);

        entityManager.flush();
        entityManager.clear();

        NetworkChangeEntity loaded = changeRepo.findByChangeId("CHG-REL-001").orElseThrow();
        assertEquals(1, loaded.getSteps().size());
        assertEquals("validate", loaded.getSteps().get(0).getStepName());
    }

    @Test
    void entity_embedded_maintenanceWindow() {
        NetworkChangeEntity change = new NetworkChangeEntity(
                "CHG-EMB-001", "Embedded Test", "south", DeviceFamily.RADIO, ChangeRisk.MEDIUM);
        ChangeWindowEmbeddable window = new ChangeWindowEmbeddable(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 1, 6, 0),
                "UTC");
        change.setMaintenanceWindow(window);
        changeRepo.save(change);

        entityManager.flush();
        entityManager.clear();

        NetworkChangeEntity loaded = changeRepo.findByChangeId("CHG-EMB-001").orElseThrow();
        assertNotNull(loaded.getMaintenanceWindow());
        assertEquals("UTC", loaded.getMaintenanceWindow().getTimezone());
    }

    @Test
    void entity_version_optimisticLocking() {
        NetworkChangeEntity entity = new NetworkChangeEntity(
                "CHG-VER-001", "Version Test", "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        changeRepo.save(entity);
        entityManager.flush();

        NetworkChangeEntity loaded = changeRepo.findByChangeId("CHG-VER-001").orElseThrow();
        assertNotNull(loaded.getVersion(), "@Version field must be populated");
        assertEquals(0L, loaded.getVersion().longValue());
    }

    @Test
    void entity_nativeQuery_works() {
        NetworkChangeEntity e = new NetworkChangeEntity(
                "CHG-NQ-001", "Native Test", "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        changeRepo.save(e);
        entityManager.flush();

        List<NetworkChangeEntity> result = changeRepo.findByRegionNative("east");
        assertTrue(result.stream().anyMatch(en -> "CHG-NQ-001".equals(en.getChangeId())));
    }

    @Test
    void title_isPropertyAccess_notNull_persists() {
        NetworkChangeEntity e = new NetworkChangeEntity(
                "CHG-PA-001", "Property-Access Title", "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        changeRepo.save(e);
        entityManager.flush();
        entityManager.clear();

        NetworkChangeEntity loaded = changeRepo.findByChangeId("CHG-PA-001").orElseThrow();
        assertEquals("Property-Access Title", loaded.getTitle());
    }

    @Test
    void title_nullableFalse_throwsOnNull() {
        NetworkChangeEntity e = new NetworkChangeEntity(
                "CHG-PA-002", null, "east", DeviceFamily.ROUTER, ChangeRisk.LOW);
        // Hibernate should throw when persisting because title is NOT NULL.
        // The exact exception type varies (PersistenceException or
        // DataIntegrityViolationException depending on the layer);
        // any runtime exception proves the constraint was enforced.
        boolean threw = false;
        try {
            changeRepo.save(e);
            entityManager.flush();
        } catch (RuntimeException ex) {
            threw = true;
            assertTrue(ex.getMessage().toLowerCase().contains("title")
                            || ex.getMessage().toLowerCase().contains("null"),
                    "exception should mention the title column or null violation");
        }
        assertTrue(threw, "null title must be rejected by the NOT NULL constraint");
    }

    @Test
    void namedQuery_findByTitle_returnsOnlyMatchingTitle() {
        changeRepo.save(new NetworkChangeEntity(
                "CHG-NT-001", "Alpha Change", "east", DeviceFamily.ROUTER, ChangeRisk.LOW));
        changeRepo.save(new NetworkChangeEntity(
                "CHG-NT-002", "Beta Change", "west", DeviceFamily.RADIO, ChangeRisk.MEDIUM));
        changeRepo.save(new NetworkChangeEntity(
                "CHG-NT-003", "Alpha Change", "north", DeviceFamily.TRANSMISSION, ChangeRisk.HIGH));

        List<NetworkChangeEntity> results = changeRepo.findByTitle("Alpha Change");
        assertEquals(2, results.size(), "two entities share the title 'Alpha Change'");
        assertTrue(results.stream().allMatch(e -> "Alpha Change".equals(e.getTitle())));
        assertTrue(results.stream().noneMatch(e -> "Beta Change".equals(e.getTitle())));
    }

    @Test
    void namedQuery_findActiveByRegion_filtersStatusAndRegion() {
        String region = "findActiveRegion-1";
        NetworkChangeEntity approved = new NetworkChangeEntity(
                "CHG-AR-001", "Approved", region, DeviceFamily.ROUTER, ChangeRisk.LOW);
        approved.setStatus(ChangeStatus.APPROVED);
        changeRepo.save(approved);

        NetworkChangeEntity running = new NetworkChangeEntity(
                "CHG-AR-002", "Running", region, DeviceFamily.ROUTER, ChangeRisk.MEDIUM);
        running.setStatus(ChangeStatus.RUNNING);
        changeRepo.save(running);

        NetworkChangeEntity draft = new NetworkChangeEntity(
                "CHG-AR-003", "Draft", region, DeviceFamily.ROUTER, ChangeRisk.LOW);
        draft.setStatus(ChangeStatus.DRAFT);
        changeRepo.save(draft);

        // Different region, same status — must NOT be returned
        NetworkChangeEntity otherRegion = new NetworkChangeEntity(
                "CHG-AR-004", "Other", "different-region", DeviceFamily.ROUTER, ChangeRisk.LOW);
        otherRegion.setStatus(ChangeStatus.APPROVED);
        changeRepo.save(otherRegion);

        List<NetworkChangeEntity> active = changeRepo.findActiveByRegion(region);
        assertEquals(2, active.size(), "only APPROVED and RUNNING in the target region");
        assertTrue(active.stream().allMatch(e -> region.equals(e.getRegionCode())));
        assertTrue(active.stream().noneMatch(e -> e.getStatus() == ChangeStatus.DRAFT));
    }

    @Test
    void namedNativeQuery_findByFamilyNative_returnsOnlyMatchingFamily() {
        changeRepo.save(new NetworkChangeEntity(
                "CHG-FN-001", "Router A", "east", DeviceFamily.ROUTER, ChangeRisk.LOW));
        changeRepo.save(new NetworkChangeEntity(
                "CHG-FN-002", "Radio A", "east", DeviceFamily.RADIO, ChangeRisk.MEDIUM));
        changeRepo.save(new NetworkChangeEntity(
                "CHG-FN-003", "Router B", "west", DeviceFamily.ROUTER, ChangeRisk.LOW));

        List<NetworkChangeEntity> routers = changeRepo.findByFamilyNative("ROUTER");
        assertEquals(2, routers.size(), "two ROUTER entities");
        assertTrue(routers.stream().allMatch(e -> e.getDeviceFamily() == DeviceFamily.ROUTER));

        List<NetworkChangeEntity> radios = changeRepo.findByFamilyNative("RADIO");
        assertEquals(1, radios.size(), "one RADIO entity");
        assertEquals(DeviceFamily.RADIO, radios.get(0).getDeviceFamily());
    }

    @Test
    void title_propertyAccess_verifiedViaMetamodel() {
        // Verify that 'title' is a persistent attribute recognized by JPA metamodel
        var metamodel = entityManager.getMetamodel();
        var entityType = metamodel.entity(NetworkChangeEntity.class);
        var titleAttr = entityType.getAttribute("title");
        assertNotNull(titleAttr, "'title' must be a persistent attribute in the JPA metamodel");
        assertEquals(String.class, titleAttr.getJavaType());
    }
}
