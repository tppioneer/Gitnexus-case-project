package com.example.telecom.change.repository;

import com.example.telecom.change.domain.NetworkChangePlan;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction over plan storage — allows in-memory and JPA-backed implementations.
 * The JPA repository handles entities; this interface works with the domain model.
 */
public interface ChangeSnapshotStore {
    void save(NetworkChangePlan plan);
    Optional<NetworkChangePlan> findByPlanId(String planId);
    List<NetworkChangePlan> findAll();
    void deleteByPlanId(String planId);
}
