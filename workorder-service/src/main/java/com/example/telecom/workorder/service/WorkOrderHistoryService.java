package com.example.telecom.workorder.service;

import com.example.telecom.workorder.domain.WorkOrderHistory;
import com.example.telecom.workorder.repository.WorkOrderHistoryRepository;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.*;
import java.util.stream.Collectors;

public class WorkOrderHistoryService {

    private final WorkOrderHistoryRepository historyRepository;
    private final WorkOrderRepository workOrderRepository;

    public WorkOrderHistoryService(WorkOrderHistoryRepository historyRepository,
                                   WorkOrderRepository workOrderRepository) {
        this.historyRepository = historyRepository;
        this.workOrderRepository = workOrderRepository;
    }

    public WorkOrderHistory recordChange(String workOrderId, String fieldName, String oldValue,
                                          String newValue, String operatorId, String comment) {
        if (workOrderRepository.findById(workOrderId).isEmpty()) {
            throw new NoSuchElementException("Work order not found: " + workOrderId);
        }

        String historyId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        WorkOrderHistory history = new WorkOrderHistory(
                historyId,
                workOrderId,
                fieldName,
                oldValue,
                newValue,
                operatorId,
                comment,
                now
        );

        historyRepository.save(history);
        return history;
    }

    public Optional<WorkOrderHistory> getHistory(String historyId) {
        return historyRepository.findById(historyId);
    }

    public List<WorkOrderHistory> getHistoryByWorkOrderId(String workOrderId) {
        return historyRepository.findByWorkOrderId(workOrderId);
    }

    public List<WorkOrderHistory> getHistoryByFieldName(String fieldName) {
        return historyRepository.findByFieldName(fieldName);
    }

    public List<WorkOrderHistory> getHistoryByOperatorId(String operatorId) {
        return historyRepository.findByOperatorId(operatorId);
    }

    public boolean deleteHistory(String historyId) {
        return historyRepository.deleteById(historyId);
    }

    public Optional<WorkOrderHistory> getLatestHistory(String workOrderId) {
        return historyRepository.findByWorkOrderId(workOrderId).stream()
                .max(Comparator.comparingLong(WorkOrderHistory::getCreatedTime));
    }

    public List<WorkOrderHistory> getHistorySince(long timestamp) {
        return historyRepository.findAll().stream()
                .filter(h -> h.getCreatedTime() >= timestamp)
                .sorted(Comparator.comparingLong(WorkOrderHistory::getCreatedTime).reversed())
                .collect(Collectors.toList());
    }

    public List<WorkOrderHistory> getChangeHistoryByField(String workOrderId, String fieldName) {
        return historyRepository.findByWorkOrderId(workOrderId).stream()
                .filter(h -> fieldName.equals(h.getFieldName()))
                .sorted(Comparator.comparingLong(WorkOrderHistory::getCreatedTime))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getChangeCountByField(String workOrderId) {
        return historyRepository.findByWorkOrderId(workOrderId).stream()
                .collect(Collectors.groupingBy(
                        WorkOrderHistory::getFieldName,
                        Collectors.counting()));
    }

    public long countChangesByWorkOrder(String workOrderId) {
        return historyRepository.findByWorkOrderId(workOrderId).size();
    }

    public List<WorkOrderHistory> getStatusTransitionHistory(String workOrderId) {
        return historyRepository.findByWorkOrderId(workOrderId).stream()
                .filter(h -> "status".equals(h.getFieldName()))
                .sorted(Comparator.comparingLong(WorkOrderHistory::getCreatedTime))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getOperatorActivitySummary() {
        return historyRepository.findAll().stream()
                .filter(h -> h.getOperatorId() != null)
                .collect(Collectors.groupingBy(
                        WorkOrderHistory::getOperatorId,
                        Collectors.counting()));
    }

    public List<WorkOrderHistory> getHistoryByDateRange(long from, long to) {
        return historyRepository.findAll().stream()
                .filter(h -> h.getCreatedTime() >= from && h.getCreatedTime() <= to)
                .sorted(Comparator.comparingLong(WorkOrderHistory::getCreatedTime).reversed())
                .collect(Collectors.toList());
    }

    public Map<String, Object> getWorkOrderTimeline(String workOrderId) {
        List<WorkOrderHistory> histories = historyRepository.findByWorkOrderId(workOrderId).stream()
                .sorted(Comparator.comparingLong(WorkOrderHistory::getCreatedTime))
                .collect(Collectors.toList());

        List<Map<String, String>> timeline = new ArrayList<>();
        for (WorkOrderHistory h : histories) {
            Map<String, String> entry = new HashMap<>();
            entry.put("field", h.getFieldName());
            entry.put("from", h.getOldValue());
            entry.put("to", h.getNewValue());
            entry.put("operator", h.getOperatorId());
            entry.put("comment", h.getComment());
            entry.put("timestamp", String.valueOf(h.getCreatedTime()));
            timeline.add(entry);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("workOrderId", workOrderId);
        result.put("totalChanges", histories.size());
        result.put("timeline", timeline);
        return result;
    }
}
