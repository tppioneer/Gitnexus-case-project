package com.example.telecom.sla.repository;

import com.example.telecom.sla.domain.SlaReport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SlaReportRepository {

    private final Map<String, SlaReport> store = new ConcurrentHashMap<>();

    public SlaReport save(SlaReport report) {
        store.put(report.getReportId(), report);
        return report;
    }

    public SlaReport findById(String reportId) {
        return store.get(reportId);
    }

    public List<SlaReport> findByContractId(String contractId) {
        return store.values().stream()
                .filter(r -> contractId.equals(r.getContractId()))
                .collect(Collectors.toList());
    }

    public List<SlaReport> findByDateRange(LocalDate start, LocalDate end) {
        return store.values().stream()
                .filter(r -> !r.getPeriodStart().isBefore(start) && !r.getPeriodEnd().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<SlaReport> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(String reportId) {
        store.remove(reportId);
    }
}
