package com.example.telecom.sla.repository;

import com.example.telecom.common.sla.SlaMetricSnapshot;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SlaMetricRepository {

    private final Map<String, SlaMetricSnapshot> store = new ConcurrentHashMap<>();

    public SlaMetricSnapshot save(SlaMetricSnapshot snapshot) {
        store.put(snapshot.getSnapshotId(), snapshot);
        return snapshot;
    }

    public List<SlaMetricSnapshot> findByContractId(String contractId) {
        return store.values().stream()
                .filter(s -> contractId.equals(s.getContractId()))
                .collect(Collectors.toList());
    }

    public List<SlaMetricSnapshot> findByDateRange(String contractId, LocalDateTime start, LocalDateTime end) {
        return store.values().stream()
                .filter(s -> contractId.equals(s.getContractId()))
                .filter(s -> !s.getTimestamp().isBefore(start) && !s.getTimestamp().isAfter(end))
                .collect(Collectors.toList());
    }

    public SlaMetricSnapshot findLatest(String contractId) {
        return store.values().stream()
                .filter(s -> contractId.equals(s.getContractId()))
                .max((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .orElse(null);
    }

    public SlaMetricSnapshot findById(String snapshotId) {
        return store.get(snapshotId);
    }

    public List<SlaMetricSnapshot> findAll() {
        return new ArrayList<>(store.values());
    }
}
