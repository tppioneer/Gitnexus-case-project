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

    /**
     * Get a comprehensive overview of work order statistics.
     * Returns a map with keys: total, open, inProgress, resolved, closed, cancelled,
     * breached, avgResolutionMinutes, slaComplianceRate.
     */
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        List<WorkOrder> all = workOrderRepository.findAll();
        long total = all.size();
        long open = all.stream().filter(wo -> wo.getStatus() == WorkOrderStatus.CREATED).count();
        long inProgress = all.stream().filter(wo -> wo.getStatus() == WorkOrderStatus.PROCESSING).count();
        long resolved = all.stream().filter(wo -> wo.getStatus() == WorkOrderStatus.RESOLVED).count();
        long closed = all.stream().filter(wo -> wo.getStatus() == WorkOrderStatus.CLOSED).count();
        long cancelled = all.stream().filter(wo -> wo.getStatus() == WorkOrderStatus.CANCELLED).count();
        long breached = countSlaBreached();

        overview.put("total", total);
        overview.put("open", open);
        overview.put("inProgress", inProgress);
        overview.put("resolved", resolved);
        overview.put("closed", closed);
        overview.put("cancelled", cancelled);
        overview.put("breached", breached);
        overview.put("avgResolutionMinutes", averageResolutionTimeMinutes());

        double slaComplianceRate = total > 0 ? ((double) (total - breached) / total) * 100.0 : 100.0;
        overview.put("slaComplianceRate", slaComplianceRate);

        return overview;
    }

    /**
     * Get distribution of work orders by status name.
     */
    public Map<String, Long> getStatusDistribution() {
        return workOrderRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        wo -> wo.getStatus().name(),
                        Collectors.counting()
                ));
    }

    /**
     * Get distribution of work orders by priority.
     */
    public Map<WorkOrderPriority, Long> getPriorityDistribution() {
        return workOrderRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        WorkOrder::getPriority,
                        Collectors.counting()
                ));
    }

    /**
     * Get distribution of work orders by category (maintenanceRegionCode).
     */
    public Map<String, Long> getCategoryDistribution() {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getMaintenanceRegionCode() != null)
                .collect(Collectors.groupingBy(
                        WorkOrder::getMaintenanceRegionCode,
                        Collectors.counting()
                ));
    }

    /**
     * Get trend data: count of work orders created between from and to (millis), grouped by status name.
     */
    public Map<String, Long> getTrend(long from, long to) {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= from && wo.getCreatedTime() <= to)
                .collect(Collectors.groupingBy(
                        wo -> wo.getStatus().name(),
                        Collectors.counting()
                ));
    }

    /**
     * Get operator statistics: assigned count, resolved count, open count, avg resolution time (minutes).
     */
    public Map<String, Object> getOperatorStats(String operatorId) {
        List<WorkOrder> all = workOrderRepository.findAll();
        List<WorkOrder> assigned = all.stream()
                .filter(wo -> operatorId.equals(wo.getAssignee()))
                .toList();
        long assignedCount = assigned.size();
        long resolvedCount = assigned.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.RESOLVED)
                .count();
        long openCount = assigned.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.CREATED)
                .count();
        double avgResolutionMs = assigned.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.RESOLVED)
                .mapToLong(wo -> wo.getUpdatedTime() - wo.getCreatedTime())
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("assignedCount", assignedCount);
        stats.put("resolvedCount", resolvedCount);
        stats.put("openCount", openCount);
        stats.put("avgResolutionMinutes", avgResolutionMs / 60000.0);
        return stats;
    }

    /**
     * Evaluate a single work order's SLA status.
     * Returns "ON_TRACK" if within SLA, "AT_RISK" if over 80% of SLA time elapsed,
     * "BREACHED" if SLA has been exceeded.
     */
    public String evaluate(WorkOrder workOrder) {
        if (workOrder.getStatus() == WorkOrderStatus.RESOLVED
                || workOrder.getStatus() == WorkOrderStatus.CLOSED
                || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            return "ON_TRACK";
        }
        long elapsed = System.currentTimeMillis() - workOrder.getCreatedTime();
        long slaMs = getSlaMs(workOrder.getPriority());
        if (elapsed >= slaMs) {
            return "BREACHED";
        }
        if (elapsed >= slaMs * 0.8) {
            return "AT_RISK";
        }
        return "ON_TRACK";
    }
}
