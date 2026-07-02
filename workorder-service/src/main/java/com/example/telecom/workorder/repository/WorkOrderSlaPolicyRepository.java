package com.example.telecom.workorder.repository;

import com.example.telecom.common.workorder.SlaPolicy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkOrderSlaPolicyRepository {
    private final Map<String, SlaPolicy> policies = new ConcurrentHashMap<>();

    public SlaPolicy save(SlaPolicy policy) {
        policies.put(policy.getSlaPolicyId(), policy);
        return policy;
    }

    public Optional<SlaPolicy> findById(String policyId) {
        return Optional.ofNullable(policies.get(policyId));
    }

    public Optional<SlaPolicy> findByPriority(String priority) {
        return policies.values().stream()
                .filter(p -> p.getPriority().equals(priority) && p.isActive())
                .findFirst();
    }

    public List<SlaPolicy> findAll() {
        return new ArrayList<>(policies.values());
    }
}
