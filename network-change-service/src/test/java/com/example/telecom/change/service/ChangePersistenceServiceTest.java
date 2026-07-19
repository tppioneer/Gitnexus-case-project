package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.ChangeStatus;
import com.example.telecom.change.domain.DeviceFamily;
import com.example.telecom.change.entity.NetworkChangeEntity;
import com.example.telecom.change.repository.NetworkChangeJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Service-to-repository runtime oracle: exercises ChangePersistenceService
 * which in turn calls the JPA repository. Verifies end-to-end persistence,
 * named queries, and JPQL via a real Spring context (not Mockito).
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class ChangePersistenceServiceTest {

    @Autowired private ChangePersistenceService persistenceService;
    @Autowired private NetworkChangeJpaRepository jpaRepository;

    @Test
    void saveAndFindByChangeId_returnsPersistedEntity() {
        NetworkChangeEntity entity = new NetworkChangeEntity(
                "CHG-PS-001", "PS Save Test", "svc-east",
                DeviceFamily.ROUTER, ChangeRisk.LOW);
        persistenceService.save(entity);

        NetworkChangeEntity found = persistenceService.findByChangeId("CHG-PS-001");
        assertNotNull(found);
        assertEquals("PS Save Test", found.getTitle());
        assertEquals(DeviceFamily.ROUTER, found.getDeviceFamily());
    }

    @Test
    void findByTitle_serviceLayer_returnsOnlyMatchingTitle() {
        jpaRepository.save(new NetworkChangeEntity(
                "CHG-PS-002", "Service Title Match", "svc-west",
                DeviceFamily.RADIO, ChangeRisk.MEDIUM));
        jpaRepository.save(new NetworkChangeEntity(
                "CHG-PS-003", "Service Title Other", "svc-west",
                DeviceFamily.RADIO, ChangeRisk.LOW));

        List<NetworkChangeEntity> results = persistenceService.findByTitle("Service Title Match");
        assertEquals(1, results.size());
        assertEquals("Service Title Match", results.get(0).getTitle());
    }

    @Test
    void findActiveByRegion_serviceLayer_returnsOnlyApprovedAndRunning() {
        String region = "svc-active-region";
        NetworkChangeEntity approved = new NetworkChangeEntity(
                "CHG-PS-004", "Approved", region, DeviceFamily.ROUTER, ChangeRisk.LOW);
        approved.setStatus(ChangeStatus.APPROVED);
        jpaRepository.save(approved);

        NetworkChangeEntity running = new NetworkChangeEntity(
                "CHG-PS-005", "Running", region, DeviceFamily.TRANSMISSION, ChangeRisk.MEDIUM);
        running.setStatus(ChangeStatus.RUNNING);
        jpaRepository.save(running);

        NetworkChangeEntity draft = new NetworkChangeEntity(
                "CHG-PS-006", "Draft", region, DeviceFamily.RADIO, ChangeRisk.LOW);
        draft.setStatus(ChangeStatus.DRAFT);
        jpaRepository.save(draft);

        List<NetworkChangeEntity> active = persistenceService.findActiveByRegion(region);
        assertEquals(2, active.size());
        assertTrue(active.stream().noneMatch(e -> e.getStatus() == ChangeStatus.DRAFT));
    }

    @Test
    void findByFamilyNative_serviceLayer_returnsOnlyMatchingFamily() {
        jpaRepository.save(new NetworkChangeEntity(
                "CHG-PS-007", "Svc Router", "svc-north",
                DeviceFamily.ROUTER, ChangeRisk.LOW));
        jpaRepository.save(new NetworkChangeEntity(
                "CHG-PS-008", "Svc Radio", "svc-north",
                DeviceFamily.RADIO, ChangeRisk.MEDIUM));

        List<NetworkChangeEntity> routers = persistenceService.findByFamilyNative("ROUTER");
        assertTrue(routers.stream().anyMatch(e -> "CHG-PS-007".equals(e.getChangeId())));
        assertTrue(routers.stream().noneMatch(e -> e.getDeviceFamily() == DeviceFamily.RADIO));
    }

    @Test
    void findByRegionAndStatus_serviceLayer_filtersCorrectly() {
        jpaRepository.save(new NetworkChangeEntity(
                "CHG-PS-009", "Svc Filter", "svc-filter",
                DeviceFamily.ROUTER, ChangeRisk.LOW));

        List<NetworkChangeEntity> found = persistenceService.findByRegionAndStatus(
                "svc-filter", ChangeStatus.DRAFT);
        assertEquals(1, found.size());
        assertEquals("Svc Filter", found.get(0).getTitle());

        List<NetworkChangeEntity> notFound = persistenceService.findByRegionAndStatus(
                "svc-filter", ChangeStatus.COMPLETED);
        assertEquals(0, notFound.size());
    }
}
