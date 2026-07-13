package com.example.telecom.alarm.federated;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class FederatedAlarmSourceRegistry {

    private final ConcurrentHashMap<String, FederatedAlarmSource> registry = new ConcurrentHashMap<>();

    public Optional<FederatedAlarmSource> resolve(String sourceId) {
        return Optional.ofNullable(registry.get(sourceId));
    }

    public void register(FederatedAlarmSource source) {
        registry.put(source.getSourceId(), source);
    }

    public Optional<FederatedAlarmSource> unregister(String sourceId) {
        return Optional.ofNullable(registry.remove(sourceId));
    }

    public List<FederatedAlarmSource> getAllSources() {
        return new ArrayList<>(registry.values());
    }

    public List<FederatedAlarmSource> getSourcesByRegion(String regionCode) {
        return registry.values().stream()
                .filter(s -> regionCode.equals(s.getRegionCode()))
                .collect(Collectors.toList());
    }
}
