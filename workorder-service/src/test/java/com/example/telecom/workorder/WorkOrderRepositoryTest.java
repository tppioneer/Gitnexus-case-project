package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderRepositoryTest {

    private WorkOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new WorkOrderRepository();

        repository.save(new WorkOrder("wo-1", "a1", "dev-1", WorkOrderStatus.ASSIGNED,
                WorkOrderPriority.HIGH, "WO 1", "desc", "EAST", 1000L));
        repository.save(new WorkOrder("wo-2", "a2", "dev-1", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "WO 2", "desc", "EAST", 2000L));
        repository.save(new WorkOrder("wo-3", "a3", "dev-2", WorkOrderStatus.CREATED,
                WorkOrderPriority.LOW, "WO 3", "desc", "WEST", 3000L));

        WorkOrder woAssigned = new WorkOrder("wo-4", "a4", "dev-2", WorkOrderStatus.ASSIGNED,
                WorkOrderPriority.MEDIUM, "WO 4", "desc", "WEST", 4000L);
        woAssigned.setAssignee("op-1");
        repository.save(woAssigned);
    }

    @Test
    void shouldFindById() {
        assertTrue(repository.findById("wo-1").isPresent());
        assertEquals("WO 1", repository.findById("wo-1").get().getTitle());
    }

    @Test
    void shouldFindByDeviceId() {
        assertEquals(2, repository.findByDeviceId("dev-1").size());
        assertEquals(2, repository.findByDeviceId("dev-2").size());
    }

    @Test
    void shouldFindByAssignee() {
        assertEquals(1, repository.findByAssignee("op-1").size());
        assertTrue(repository.findByAssignee("nonexistent").isEmpty());
    }

    @Test
    void shouldFindAll() {
        assertEquals(4, repository.findAll().size());
    }

    @Test
    void shouldReturnEmptyForMissingId() {
        assertTrue(repository.findById("nonexistent").isEmpty());
    }
}
