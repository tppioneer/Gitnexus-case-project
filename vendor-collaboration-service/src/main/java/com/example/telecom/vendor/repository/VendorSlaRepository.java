package com.example.telecom.vendor.repository;

import com.example.telecom.vendor.domain.VendorSlaReport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VendorSlaRepository {

    private final ConcurrentHashMap<String, VendorSlaReport> store = new ConcurrentHashMap<>();

    public VendorSlaReport save(VendorSlaReport report) {
        store.put(report.getReportId(), report);
        return report;
    }

    public Optional<VendorSlaReport> findById(String reportId) {
        return Optional.ofNullable(store.get(reportId));
    }

    public List<VendorSlaReport> findByVendorId(String vendorId) {
        return store.values().stream()
                .filter(r -> r.getVendorId().equals(vendorId))
                .sorted(Comparator.comparing(VendorSlaReport::getGeneratedTime).reversed())
                .collect(Collectors.toList());
    }

    public List<VendorSlaReport> findByDateRange(LocalDate from, LocalDate to) {
        return store.values().stream()
                .filter(r -> !r.getPeriodFrom().isBefore(from) && !r.getPeriodTo().isAfter(to))
                .collect(Collectors.toList());
    }

    public List<VendorSlaReport> findByStatus(com.example.telecom.common.vendor.VendorSlaStatus status) {
        return store.values().stream()
                .filter(r -> r.getSlaStatus() == status)
                .collect(Collectors.toList());
    }

    public Optional<VendorSlaReport> findLatest(String vendorId) {
        return store.values().stream()
                .filter(r -> r.getVendorId().equals(vendorId))
                .max(Comparator.comparing(VendorSlaReport::getGeneratedTime));
    }

    public List<VendorSlaReport> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(String reportId) {
        store.remove(reportId);
    }

    public long count() {
        return store.size();
    }

    public long countByVendorId(String vendorId) {
        return store.values().stream()
                .filter(r -> r.getVendorId().equals(vendorId))
                .count();
    }
}
