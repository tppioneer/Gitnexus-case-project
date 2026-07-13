package com.example.telecom.dispatch.repository;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.dispatch.domain.DispatchHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DispatchHistoryRepository {

    private final ConcurrentHashMap<String, DispatchHistory> store = new ConcurrentHashMap<>();

    public DispatchHistory save(DispatchHistory history) {
        if (history.getHistoryId() == null) {
            history.setHistoryId(UUID.randomUUID().toString());
        }
        store.put(history.getHistoryId(), history);
        return history;
    }

    public DispatchHistory findById(String id) {
        DispatchHistory history = store.get(id);
        if (history == null) {
            throw new DomainException("NOT_FOUND", "Dispatch history not found: " + id);
        }
        return history;
    }

    public List<DispatchHistory> findByOrderId(String orderId) {
        return store.values().stream()
                .filter(h -> orderId.equals(h.getOrderId()))
                .collect(Collectors.toList());
    }

    public List<DispatchHistory> findByOperatorId(String operatorId) {
        return store.values().stream()
                .filter(h -> operatorId.equals(h.getAssigneeId()) || operatorId.equals(h.getPreviousAssigneeId()))
                .collect(Collectors.toList());
    }

    public List<DispatchHistory> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return store.values().stream()
                .filter(h -> {
                    LocalDateTime ts = h.getTimestamp();
                    return ts != null && !ts.isBefore(from) && !ts.isAfter(to);
                })
                .collect(Collectors.toList());
    }

    public List<DispatchHistory> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<DispatchHistory> getRecent(int limit) {
        return store.values().stream()
                .sorted((a, b) -> {
                    LocalDateTime ta = a.getTimestamp();
                    LocalDateTime tb = b.getTimestamp();
                    if (ta == null && tb == null) return 0;
                    if (ta == null) return 1;
                    if (tb == null) return -1;
                    return tb.compareTo(ta);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    public long count() {
        return store.size();
    }
}
