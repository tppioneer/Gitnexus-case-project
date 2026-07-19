package com.example.telecom.change.service;

import com.example.telecom.change.annotation.OpsReadEndpoint;
import com.example.telecom.change.annotation.RegionScope;
import com.example.telecom.change.domain.NetworkChangePlan;
import com.example.telecom.change.repository.ChangeSnapshotStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Read-only query service. Demonstrates @Transactional(readOnly=true).
 */
@Service
public class ChangePlanQueryService {

    private final ChangeSnapshotStore snapshotStore;

    public ChangePlanQueryService(ChangeSnapshotStore snapshotStore) {
        this.snapshotStore = snapshotStore;
    }

    @Transactional(readOnly = true)
    public Optional<NetworkChangePlan> findById(String planId) {
        return snapshotStore.findByPlanId(planId);
    }

    @Transactional(readOnly = true)
    public List<NetworkChangePlan> findAll() {
        return snapshotStore.findAll();
    }

    /**
     * Demonstrates @RegionScope on a parameter — the annotation
     * applies to the regionCode parameter, not the method.
     */
    @OpsReadEndpoint(path = "/region-query")
    public List<NetworkChangePlan> findByRegion(@RegionScope("east") String regionCode) {
        return snapshotStore.findAll().stream()
                .filter(p -> regionCode.equals(p.getRegionCode()))
                .toList();
    }
}
