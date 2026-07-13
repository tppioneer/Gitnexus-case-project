package com.example.telecom.alarm.federated.repository;

import com.example.telecom.alarm.federated.FederatedAlarmSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class FederatedAlarmSourceRepository {

    private final ConcurrentHashMap<String, FederatedAlarmSource> store = new ConcurrentHashMap<>();

    public FederatedAlarmSource save(FederatedAlarmSource source) {
        store.put(source.getSourceId(), source);
        return source;
    }

    public Optional<FederatedAlarmSource> findById(String sourceId) {
        return Optional.ofNullable(store.get(sourceId));
    }

    public List<FederatedAlarmSource> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<FederatedAlarmSource> findByRegion(String regionCode) {
        return store.values().stream()
                .filter(s -> regionCode.equals(s.getRegionCode()))
                .collect(Collectors.toList());
    }

    public Optional<FederatedAlarmSource> updateStatus(String sourceId, String status) {
        return findById(sourceId).map(source -> {
            source.setStatus(status);
            source.setLastHeartbeat(LocalDateTime.now());
            return source;
        });
    }

    public boolean delete(String sourceId) {
        return store.remove(sourceId) != null;
    }
}
