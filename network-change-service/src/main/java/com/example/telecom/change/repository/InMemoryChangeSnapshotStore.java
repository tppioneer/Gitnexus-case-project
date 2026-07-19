package com.example.telecom.change.repository;

import com.example.telecom.change.domain.NetworkChangePlan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@code ChangeSnapshotStore} implementation.
 * Used as the default store and in tests that don't need JPA.
 */
@Component
public class InMemoryChangeSnapshotStore implements ChangeSnapshotStore {

    private final Map<String, NetworkChangePlan> store = new ConcurrentHashMap<>();

    @Override
    public void save(NetworkChangePlan plan) {
        store.put(plan.getPlanId(), plan);
    }

    @Override
    public Optional<NetworkChangePlan> findByPlanId(String planId) {
        return Optional.ofNullable(store.get(planId));
    }

    @Override
    public List<NetworkChangePlan> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteByPlanId(String planId) {
        store.remove(planId);
    }
}
