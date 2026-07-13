package com.example.telecom.vendor.repository;

import com.example.telecom.vendor.dto.VendorPerformanceReport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VendorPerformanceRepository {

    private final ConcurrentHashMap<String, VendorPerformanceReport> store = new ConcurrentHashMap<>();

    public VendorPerformanceReport save(VendorPerformanceReport report) {
        store.put(report.getReportId(), report);
        return report;
    }

    public Optional<VendorPerformanceReport> findById(String reportId) {
        return Optional.ofNullable(store.get(reportId));
    }

    public List<VendorPerformanceReport> findByVendorId(String vendorId) {
        return store.values().stream()
                .filter(r -> r.getVendorId().equals(vendorId))
                .collect(Collectors.toList());
    }

    public List<VendorPerformanceReport> findByDateRange(LocalDate from, LocalDate to) {
        return store.values().stream()
                .filter(r -> !r.getPeriodFrom().isBefore(from) && !r.getPeriodTo().isAfter(to))
                .collect(Collectors.toList());
    }

    public Optional<VendorPerformanceReport> findLatest(String vendorId) {
        return store.values().stream()
                .filter(r -> r.getVendorId().equals(vendorId))
                .max(Comparator.comparing(VendorPerformanceReport::getGeneratedTime));
    }

    public List<VendorPerformanceReport> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<VendorPerformanceReport> getRanking() {
        return store.values().stream()
                .sorted(Comparator.comparingDouble(VendorPerformanceReport::getOverallScore).reversed())
                .collect(Collectors.toList());
    }
}
