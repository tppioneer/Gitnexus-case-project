package com.example.telecom.dispatch.repository;

import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.common.dispatch.DispatchPriority;
import com.example.telecom.common.exception.DomainException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DispatchRepository {

    private final ConcurrentHashMap<String, DispatchOrder> store = new ConcurrentHashMap<>();

    public DispatchOrder save(DispatchOrder order) {
        store.put(order.getOrderId(), order);
        return order;
    }

    public DispatchOrder findById(String id) {
        DispatchOrder order = store.get(id);
        if (order == null) {
            throw new DomainException("NOT_FOUND", "Order not found: " + id);
        }
        return order;
    }

    public List<DispatchOrder> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<DispatchOrder> findByStatus(String status) {
        return store.values().stream()
                .filter(order -> order.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<DispatchOrder> findByAssignee(String assigneeId) {
        return store.values().stream()
                .filter(order -> assigneeId.equals(order.getAssigneeId()))
                .collect(Collectors.toList());
    }

    public List<DispatchOrder> findByPriority(DispatchPriority priority) {
        return store.values().stream()
                .filter(order -> order.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<DispatchOrder> findByRegion(String regionCode) {
        return store.values().stream()
                .filter(order -> regionCode.equals(order.getTargetRegionCode()))
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        store.remove(id);
    }

    public long count() {
        return store.size();
    }

    public List<DispatchOrder> findBySkill(String skill) {
        return store.values().stream()
                .filter(order -> skill.equals(order.getSkillRequired()))
                .collect(Collectors.toList());
    }

    public List<DispatchOrder> findByEstimatedDuration(int min, int max) {
        return store.values().stream()
                .filter(order -> order.getEstimatedDuration() >= min && order.getEstimatedDuration() <= max)
                .collect(Collectors.toList());
    }

    public List<DispatchOrder> findRecentOrders(int limit) {
        return store.values().stream()
                .sorted((a, b) -> {
                    if (a.getCreatedTime() == null && b.getCreatedTime() == null) return 0;
                    if (a.getCreatedTime() == null) return 1;
                    if (b.getCreatedTime() == null) return -1;
                    return b.getCreatedTime().compareTo(a.getCreatedTime());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map<String, Long> countByStatus() {
        return store.values().stream()
                .collect(Collectors.groupingBy(DispatchOrder::getStatus, Collectors.counting()));
    }
}
