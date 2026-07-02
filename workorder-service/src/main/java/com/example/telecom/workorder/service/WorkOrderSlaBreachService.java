package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.*;
import com.example.telecom.workorder.event.SlaBreachEventPublisher;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.repository.WorkOrderSlaPolicyRepository;

import java.util.*;

public class WorkOrderSlaBreachService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderSlaPolicyRepository slaPolicyRepository;
    private final SlaBreachEventPublisher slaBreachEventPublisher;

    public WorkOrderSlaBreachService(WorkOrderRepository workOrderRepository,
                                      WorkOrderSlaPolicyRepository slaPolicyRepository,
                                      SlaBreachEventPublisher slaBreachEventPublisher) {
        this.workOrderRepository = workOrderRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.slaBreachEventPublisher = slaBreachEventPublisher;
    }

    public List<SlaBreachEvent> checkAllWorkOrders() {
        List<SlaBreachEvent> breaches = new ArrayList<>();
        List<WorkOrder> openOrders = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() != WorkOrderStatus.CLOSED
                        && wo.getStatus() != WorkOrderStatus.CANCELLED)
                .toList();

        for (WorkOrder wo : openOrders) {
            Optional<SlaPolicy> policy = slaPolicyRepository.findByPriority(wo.getPriority().name());
            if (policy.isPresent()) {
                SlaPolicy slaPolicy = policy.get();
                if (slaPolicy.isBreachResponse(System.currentTimeMillis() - wo.getCreatedTime())) {
                    SlaBreachEvent event = slaBreachEventPublisher.createBreachEvent(
                            wo, slaPolicy, "RESPONSE");
                    breaches.add(event);
                    slaBreachEventPublisher.publish(event);
                }
            }
        }
        return breaches;
    }

    public boolean isAnySlaBreached(String workOrderId) {
        return workOrderRepository.findById(workOrderId).map(wo -> {
            Optional<SlaPolicy> policy = slaPolicyRepository.findByPriority(wo.getPriority().name());
            return policy.map(p -> p.isBreachResponse(
                    System.currentTimeMillis() - wo.getCreatedTime())).orElse(false);
        }).orElse(false);
    }
}
