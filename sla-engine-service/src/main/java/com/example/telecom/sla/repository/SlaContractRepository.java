package com.example.telecom.sla.repository;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.vendor.VendorSlaStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SlaContractRepository {

    private final Map<String, SlaContract> store = new ConcurrentHashMap<>();

    public SlaContract save(SlaContract contract) {
        store.put(contract.getContractId(), contract);
        return contract;
    }

    public SlaContract findById(String contractId) {
        return store.get(contractId);
    }

    public List<SlaContract> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<SlaContract> findByVendorId(String vendorId) {
        return store.values().stream()
                .filter(c -> vendorId.equals(c.getVendorId()))
                .collect(Collectors.toList());
    }

    public List<SlaContract> findByStatus(VendorSlaStatus status) {
        return store.values().stream()
                .filter(c -> status.equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    public List<SlaContract> findByRegion(String regionCode) {
        return store.values().stream()
                .filter(c -> regionCode.equals(c.getRegionCode()))
                .collect(Collectors.toList());
    }

    public SlaContract delete(String contractId) {
        return store.remove(contractId);
    }

    public long count() {
        return store.size();
    }
}
