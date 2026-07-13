package com.example.telecom.alarm.federated.repository;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.FederatedAlarmStatus;
import com.example.telecom.common.alarm.Severity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class FederatedAlarmRepository {

    private final ConcurrentHashMap<String, FederatedAlarmRecord> store = new ConcurrentHashMap<>();

    public FederatedAlarmRecord save(FederatedAlarmRecord record) {
        record.setUpdatedAt(LocalDateTime.now());
        store.put(record.getAlarmId(), record);
        return record;
    }

    public Optional<FederatedAlarmRecord> findById(String alarmId) {
        return Optional.ofNullable(store.get(alarmId));
    }

    public List<FederatedAlarmRecord> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<FederatedAlarmRecord> findBySource(String sourceId) {
        return store.values().stream()
                .filter(r -> sourceId.equals(r.getSourceId()))
                .collect(Collectors.toList());
    }

    public List<FederatedAlarmRecord> findByRegion(String regionCode) {
        return store.values().stream()
                .filter(r -> regionCode.equals(r.getRegionCode()))
                .collect(Collectors.toList());
    }

    public List<FederatedAlarmRecord> findBySeverity(Severity severity) {
        return store.values().stream()
                .filter(r -> severity == r.getSeverity())
                .collect(Collectors.toList());
    }

    public List<FederatedAlarmRecord> findByStatus(FederatedAlarmStatus status) {
        return store.values().stream()
                .filter(r -> status == r.getStatus())
                .collect(Collectors.toList());
    }

    public List<FederatedAlarmRecord> findByTimeRange(LocalDateTime from, LocalDateTime to) {
        return store.values().stream()
                .filter(r -> !r.getAlarmTime().isBefore(from) && !r.getAlarmTime().isAfter(to))
                .collect(Collectors.toList());
    }

    public boolean delete(String alarmId) {
        return store.remove(alarmId) != null;
    }

    public long count() {
        return store.size();
    }
}
