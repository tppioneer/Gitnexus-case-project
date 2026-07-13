package com.example.telecom.sla.repository;

import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.domain.SlaBreach;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SlaBreachRepository {

    private final Map<String, SlaBreach> store = new ConcurrentHashMap<>();

    public SlaBreach save(SlaBreach breach) {
        store.put(breach.getBreachId(), breach);
        return breach;
    }

    public SlaBreach findById(String breachId) {
        return store.get(breachId);
    }

    public List<SlaBreach> findByContractId(String contractId) {
        return store.values().stream()
                .filter(b -> contractId.equals(b.getContractId()))
                .collect(Collectors.toList());
    }

    public List<SlaBreach> findActiveBreaches() {
        return store.values().stream()
                .filter(b -> b.getStatus() == VendorSlaStatus.BREACHED)
                .collect(Collectors.toList());
    }

    public List<SlaBreach> findByStatus(VendorSlaStatus status) {
        return store.values().stream()
                .filter(b -> status.equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    public List<SlaBreach> findAll() {
        return new ArrayList<>(store.values());
    }

    public long count() {
        return store.size();
    }
}
