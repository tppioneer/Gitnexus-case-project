package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderPriority;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.time.*;
import java.util.*;
import java.util.stream.*;

public class WorkOrderTrendService {

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderTrendService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    public Map<String, Long> analyze(long from, long to) {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= from && wo.getCreatedTime() <= to)
                .collect(Collectors.groupingBy(
                        wo -> wo.getStatus().name(),
                        Collectors.counting()));
    }

    public double getDailyCreationRate() {
        List<WorkOrder> all = workOrderRepository.findAll();
        if (all.isEmpty()) {
            return 0.0;
        }
        long total = all.size();
        long firstCreated = all.stream()
                .mapToLong(WorkOrder::getCreatedTime)
                .min()
                .orElse(System.currentTimeMillis());
        long now = System.currentTimeMillis();
        long daysSinceFirst = Math.max(1, (now - firstCreated) / (24 * 60 * 60 * 1000L));
        return (double) total / daysSinceFirst;
    }

    public double getAverageResolutionTime() {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.CLOSED
                        || wo.getStatus() == WorkOrderStatus.RESOLVED)
                .mapToLong(wo -> (wo.getUpdatedTime() - wo.getCreatedTime()) / 60000)
                .average()
                .orElse(0.0);
    }

    public List<Integer> getPeakHours() {
        Map<Integer, Long> hourCounts = workOrderRepository.findAll().stream()
                .map(wo -> {
                    Instant instant = Instant.ofEpochMilli(wo.getCreatedTime());
                    ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
                    return zdt.getHour();
                })
                .collect(Collectors.groupingBy(h -> h, Collectors.counting()));

        return hourCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double getSlaComplianceRate() {
        List<WorkOrder> resolvedOrClosed = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.CLOSED
                        || wo.getStatus() == WorkOrderStatus.RESOLVED)
                .collect(Collectors.toList());

        if (resolvedOrClosed.isEmpty()) {
            return 0.0;
        }

        long compliant = resolvedOrClosed.stream()
                .filter(wo -> {
                    long resolutionTime = wo.getUpdatedTime() - wo.getCreatedTime();
                    long slaMs = getSlaMs(wo.getPriority());
                    return resolutionTime <= slaMs;
                })
                .count();

        return (double) compliant / resolvedOrClosed.size();
    }

    public long getCreationCountByDateRange(long from, long to) {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= from && wo.getCreatedTime() <= to)
                .count();
    }

    public long getResolutionCountByDateRange(long from, long to) {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.RESOLVED
                        || wo.getStatus() == WorkOrderStatus.CLOSED)
                .filter(wo -> wo.getUpdatedTime() >= from && wo.getUpdatedTime() <= to)
                .count();
    }

    public Map<String, Long> getPriorityTrend(long from, long to) {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= from && wo.getCreatedTime() <= to)
                .collect(Collectors.groupingBy(
                        wo -> wo.getPriority().name(),
                        Collectors.counting()));
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
