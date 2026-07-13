package com.example.telecom.workorder.repository;

import com.example.telecom.workorder.domain.WorkOrderHistory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorkOrderHistoryRepository {
    private final Map<String, WorkOrderHistory> histories = new ConcurrentHashMap<>();

    public WorkOrderHistory save(WorkOrderHistory history) {
        histories.put(history.getHistoryId(), history);
        return history;
    }

    public Optional<WorkOrderHistory> findById(String historyId) {
        return Optional.ofNullable(histories.get(historyId));
    }

    public List<WorkOrderHistory> findByWorkOrderId(String workOrderId) {
        return histories.values().stream()
                .filter(h -> workOrderId.equals(h.getWorkOrderId()))
                .collect(Collectors.toList());
    }

    public List<WorkOrderHistory> findAll() {
        return new ArrayList<>(histories.values());
    }

    public boolean deleteById(String historyId) {
        return histories.remove(historyId) != null;
    }

    public List<WorkOrderHistory> findByDateRange(long fromEpochMillis, long toEpochMillis) {
        return histories.values().stream()
                .filter(h -> h.getCreatedTime() >= fromEpochMillis
                        && h.getCreatedTime() <= toEpochMillis)
                .collect(Collectors.toList());
    }

    public List<WorkOrderHistory> findByOperatorId(String operatorId) {
        return histories.values().stream()
                .filter(h -> operatorId.equals(h.getOperatorId()))
                .collect(Collectors.toList());
    }

    public List<WorkOrderHistory> findByFieldName(String fieldName) {
        return histories.values().stream()
                .filter(h -> fieldName.equals(h.getFieldName()))
                .collect(Collectors.toList());
    }
}
