package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.common.workorder.WorkOrderStatus;
import com.example.telecom.workorder.dto.WorkOrderReportRequest;
import com.example.telecom.workorder.dto.WorkOrderReportResponse;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorkOrderReportService {

    private final WorkOrderRepository workOrderRepository;
    private final Map<String, WorkOrderReportResponse> generatedReports = new ConcurrentHashMap<>();

    public WorkOrderReportService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    public WorkOrderReportResponse generateReport(WorkOrderReportRequest request) {
        String reportId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        Map<String, Long> summaryData = calculateSummaryData(
                request.getStartTime(),
                request.getEndTime(),
                request.getGroupBy() != null ? request.getGroupBy() : "status");

        long total = summaryData.values().stream().mapToLong(Long::longValue).sum();

        WorkOrderReportResponse response = new WorkOrderReportResponse();
        response.setReportId(reportId);
        response.setGeneratedAt(now);
        response.setStartTime(request.getStartTime());
        response.setEndTime(request.getEndTime());
        response.setTotalWorkOrders(total);
        response.setGroupBy(request.getGroupBy() != null ? request.getGroupBy() : "status");
        response.setSummaryData(summaryData);
        response.setFormat(request.getFormat() != null ? request.getFormat() : "json");

        generatedReports.put(reportId, response);
        return response;
    }

    public WorkOrderReportResponse generateSummaryReport() {
        long now = System.currentTimeMillis();
        long last24h = now - 24 * 60 * 60 * 1000L;

        String reportId = UUID.randomUUID().toString();

        Map<String, Long> summaryData = calculateSummaryData(last24h, now, "status");
        long total = summaryData.values().stream().mapToLong(Long::longValue).sum();

        WorkOrderReportResponse response = new WorkOrderReportResponse();
        response.setReportId(reportId);
        response.setGeneratedAt(now);
        response.setStartTime(last24h);
        response.setEndTime(now);
        response.setTotalWorkOrders(total);
        response.setGroupBy("status");
        response.setSummaryData(summaryData);
        response.setFormat("json");

        generatedReports.put(reportId, response);
        return response;
    }

    public String exportCsv(WorkOrderReportRequest request) {
        Map<String, Long> summaryData = calculateSummaryData(
                request.getStartTime(),
                request.getEndTime(),
                request.getGroupBy() != null ? request.getGroupBy() : "status");

        StringBuilder csv = new StringBuilder();
        csv.append("Group,Count\n");
        for (Map.Entry<String, Long> entry : summaryData.entrySet()) {
            csv.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
        }
        return csv.toString();
    }

    public Optional<WorkOrderReportResponse> getReportById(String reportId) {
        return Optional.ofNullable(generatedReports.get(reportId));
    }

    public Map<String, Long> calculateSummaryData(long startTime, long endTime, String groupBy) {
        List<WorkOrder> filtered = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= startTime && wo.getCreatedTime() <= endTime)
                .collect(Collectors.toList());

        return switch (groupBy.toLowerCase()) {
            case "priority" -> filtered.stream()
                    .collect(Collectors.groupingBy(
                            wo -> wo.getPriority().name(),
                            Collectors.counting()));
            case "region" -> filtered.stream()
                    .collect(Collectors.groupingBy(
                            WorkOrder::getMaintenanceRegionCode,
                            Collectors.counting()));
            default -> filtered.stream()
                    .collect(Collectors.groupingBy(
                            wo -> wo.getStatus().name(),
                            Collectors.counting()));
        };
    }

    public List<WorkOrderReportResponse> listAllReports() {
        return new ArrayList<>(generatedReports.values());
    }

    public boolean deleteReport(String reportId) {
        return generatedReports.remove(reportId) != null;
    }

    public WorkOrderReportResponse generateDetailedReport(long startTime, long endTime) {
        WorkOrderReportRequest request = new WorkOrderReportRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setGroupBy("status");
        request.setFormat("json");
        return generateReport(request);
    }

    public Map<String, Long> getSlaSummary(long startTime, long endTime) {
        List<WorkOrder> filtered = workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= startTime && wo.getCreatedTime() <= endTime)
                .collect(Collectors.toList());

        long total = filtered.size();
        long breached = filtered.stream()
                .filter(wo -> wo.getStatus() != WorkOrderStatus.CLOSED
                        && wo.getStatus() != WorkOrderStatus.CANCELLED)
                .filter(wo -> {
                    long slaMs = getSlaMs(wo.getPriority());
                    return System.currentTimeMillis() - wo.getCreatedTime() > slaMs;
                })
                .count();

        Map<String, Long> slaSummary = new HashMap<>();
        slaSummary.put("total", total);
        slaSummary.put("breached", breached);
        slaSummary.put("compliant", total - breached);
        return slaSummary;
    }

    public Map<String, Long> getOperatorPerformanceSummary(long startTime, long endTime) {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= startTime && wo.getCreatedTime() <= endTime)
                .filter(wo -> wo.getAssignee() != null)
                .collect(Collectors.groupingBy(
                        WorkOrder::getAssignee,
                        Collectors.counting()));
    }

    public Map<String, Long> getRegionPerformanceSummary(long startTime, long endTime) {
        return workOrderRepository.findAll().stream()
                .filter(wo -> wo.getCreatedTime() >= startTime && wo.getCreatedTime() <= endTime)
                .filter(wo -> wo.getMaintenanceRegionCode() != null)
                .collect(Collectors.groupingBy(
                        WorkOrder::getMaintenanceRegionCode,
                        Collectors.counting()));
    }

    private long getSlaMs(com.example.telecom.common.workorder.WorkOrderPriority priority) {
        return switch (priority) {
            case CRITICAL -> 15 * 60 * 1000L;
            case HIGH -> 60 * 60 * 1000L;
            case MEDIUM -> 4 * 60 * 60 * 1000L;
            case LOW -> 24 * 60 * 60 * 1000L;
        };
    }
}
