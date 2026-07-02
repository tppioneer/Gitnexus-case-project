package com.example.telecom.workorder;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.workorder.*;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.workorder.event.SlaBreachEventPublisher;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.repository.WorkOrderSlaPolicyRepository;
import com.example.telecom.workorder.service.WorkOrderSlaBreachService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderSlaBreachServiceTest {

    private WorkOrderSlaBreachService slaBreachService;
    private WorkOrderRepository workOrderRepository;
    private WorkOrderSlaPolicyRepository slaPolicyRepository;

    @BeforeEach
    void setUp() {
        workOrderRepository = new WorkOrderRepository();
        slaPolicyRepository = new WorkOrderSlaPolicyRepository();

        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(com.example.telecom.common.device.DeviceMetricEvent e) {}
            @Override public void publish(com.example.telecom.common.alarm.AlarmEvent e) {}
            @Override public void publish(WorkOrderEvent e) {}
        };

        SlaBreachEventPublisher publisher = new SlaBreachEventPublisher(bus);
        slaBreachService = new WorkOrderSlaBreachService(
                workOrderRepository, slaPolicyRepository, publisher);

        slaPolicyRepository.save(new SlaPolicy("sp-1", "CRITICAL",
                900000L, 7200000L, EscalationLevel.LEVEL_3, true));
        slaPolicyRepository.save(new SlaPolicy("sp-2", "HIGH",
                3600000L, 14400000L, EscalationLevel.LEVEL_2, true));
    }

    @Test
    void shouldDetectSlaBreachForOldWorkOrder() {
        WorkOrder oldWo = new WorkOrder("wo-old", "a1", "d1", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "Old", "desc", "EAST",
                System.currentTimeMillis() - 3600000L);
        workOrderRepository.save(oldWo);

        assertTrue(slaBreachService.isAnySlaBreached("wo-old"));
    }

    @Test
    void shouldNotDetectSlaBreachForRecentWorkOrder() {
        WorkOrder recentWo = new WorkOrder("wo-new", "a2", "d2", WorkOrderStatus.CREATED,
                WorkOrderPriority.HIGH, "New", "desc", "WEST", System.currentTimeMillis());
        workOrderRepository.save(recentWo);

        assertFalse(slaBreachService.isAnySlaBreached("wo-new"));
    }

    @Test
    void shouldCheckAllWorkOrders() {
        WorkOrder oldWo = new WorkOrder("wo-old2", "a3", "d3", WorkOrderStatus.PROCESSING,
                WorkOrderPriority.CRITICAL, "Old", "desc", "EAST",
                System.currentTimeMillis() - 7200000L);
        workOrderRepository.save(oldWo);

        List<SlaBreachEvent> breaches = slaBreachService.checkAllWorkOrders();
        assertFalse(breaches.isEmpty());
    }
}
