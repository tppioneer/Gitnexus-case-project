package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Computes work order statistics for dashboard and reporting.
 */
public class WorkOrderStatisticsService {

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderStatisticsService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    public Map<WorkOrderStatus, Long> countByStatus() {
        return workOrderRepository.findAll().stream()
                .collect(Collectors.groupingBy(WorkOrder::getStatus, Collectors.counting()));
    }

    public Map<String, Long> countByAssignee() {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getAssignee() != null)
                .collect(Collectors.groupingBy(WorkOrder::getAssignee, Collectors.counting()));
    }

    public Map<String, Long> countByRegion() {
        return workOrderRepository.findAll().stream()
                .collect(Collectors.groupingBy(WorkOrder::getMaintenanceRegionCode, Collectors.counting()));
    }

    public long countOpenWorkOrders() {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() != WorkOrderStatus.CLOSED
                        && wo.getStatus() != WorkOrderStatus.CANCELLED)
                .count();
    }

    public long countSlaBreached() {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() != WorkOrderStatus.CLOSED
                        && wo.getStatus() != WorkOrderStatus.CANCELLED)
                .filter(wo -> {
                    long slaMs = getSlaMs(wo.getPriority());
                    return System.currentTimeMillis() - wo.getCreatedTime() > slaMs;
                })
                .count();
    }

    public double averageResolutionTimeMinutes() {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.CLOSED
                        || wo.getStatus() == WorkOrderStatus.RESOLVED)
                .mapToLong(wo -> (wo.getUpdatedTime() - wo.getCreatedTime()) / 60000)
                .average()
                .orElse(0.0);
    }

    public List<WorkOrder> findStaleWorkOrders(long staleThresholdMinutes) {
        long thresholdMs = staleThresholdMinutes * 60 * 1000;
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.PROCESSING
                        || wo.getStatus() == WorkOrderStatus.ASSIGNED)
                .filter(wo -> System.currentTimeMillis() - wo.getCreatedTime() > thresholdMs)
                .toList();
    }

    public String generateReport() {
        Map<WorkOrderStatus, Long> byStatus = countByStatus();
        return String.format(
                "WO Report: total=%d, open=%d, created=%d, assigned=%d, processing=%d, resolved=%d, closed=%d, sla_breached=%d",
                workOrderRepository.findAll().size(),
                countOpenWorkOrders(),
                byStatus.getOrDefault(WorkOrderStatus.CREATED, 0L),
                byStatus.getOrDefault(WorkOrderStatus.ASSIGNED, 0L),
                byStatus.getOrDefault(WorkOrderStatus.PROCESSING, 0L),
                byStatus.getOrDefault(WorkOrderStatus.RESOLVED, 0L),
                byStatus.getOrDefault(WorkOrderStatus.CLOSED, 0L),
                countSlaBreached());
    }

    private long getSlaMs(WorkOrderPriority priority) {
        return switch (priority) {
            case CRITICAL -> 15 * 60 * 1000L;
            case HIGH -> 60 * 60 * 1000L;
            case MEDIUM -> 4 * 60 * 60 * 1000L;
            case LOW -> 24 * 60 * 60 * 1000L;
        };
    }
}
