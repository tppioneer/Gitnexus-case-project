package com.example.telecom.sla.service;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.domain.SlaCalculationResult;
import com.example.telecom.sla.monitor.SlaMonitor;
import com.example.telecom.sla.repository.SlaContractRepository;
import com.example.telecom.sla.repository.SlaMetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlaMonitoringService {

    private static final Logger log = LoggerFactory.getLogger(SlaMonitoringService.class);

    private final SlaContractRepository contractRepository;
    private final SlaMetricRepository metricRepository;
    private final SlaMonitor slaMonitor;

    public SlaMonitoringService(SlaContractRepository contractRepository,
                                SlaMetricRepository metricRepository,
                                SlaMonitor slaMonitor) {
        this.contractRepository = contractRepository;
        this.metricRepository = metricRepository;
        this.slaMonitor = slaMonitor;
    }

    public SlaMonitor.SlaMonitorResult monitor(String contractId) {
        log.info("Monitoring SLA contract: contractId={}", contractId);
        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            log.warn("SLA contract not found for monitoring: contractId={}", contractId);
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }

        if (contract.getStatus() == VendorSlaStatus.PENDING) {
            log.info("Contract is in PENDING status, skipping monitoring: contractId={}", contractId);
            throw new DomainException("CONTRACT_PENDING",
                    "Contract is not yet active. Activate before monitoring: " + contractId);
        }

        if (contract.getEndDate().isBefore(java.time.LocalDate.now())) {
            log.info("Contract has expired, skipping monitoring: contractId={}", contractId);
            throw new DomainException("CONTRACT_EXPIRED",
                    "Contract has expired: " + contractId);
        }

        SlaMonitor.SlaMonitorResult result = slaMonitor.monitor(contractId);
        log.info("Monitoring complete for contractId={}: {} metrics evaluated, {} breaches found",
                contractId, result.getTotalMetrics(), result.getBreachCount());
        return result;
    }

    public Map<String, SlaMonitor.SlaMonitorResult> checkAllContracts() {
        log.info("Starting monitoring check for all active contracts");
        List<SlaContract> contracts = contractRepository.findAll();
        Map<String, SlaMonitor.SlaMonitorResult> results = new LinkedHashMap<>();

        int monitored = 0;
        int skipped = 0;
        int failed = 0;

        for (SlaContract contract : contracts) {
            if (contract.getStatus() != VendorSlaStatus.MET
                    && contract.getStatus() != VendorSlaStatus.WARNING) {
                skipped++;
                continue;
            }
            if (contract.getEndDate().isBefore(java.time.LocalDate.now())) {
                skipped++;
                continue;
            }
            try {
                SlaMonitor.SlaMonitorResult result = slaMonitor.monitor(contract.getContractId());
                results.put(contract.getContractId(), result);
                monitored++;
            } catch (Exception e) {
                log.error("Error monitoring contract: {}", contract.getContractId(), e);
                failed++;
            }
        }

        log.info("Monitoring check completed: {} monitored, {} skipped, {} failed",
                monitored, skipped, failed);
        return results;
    }

    public Map<String, Object> getContractStatus(String contractId) {
        log.debug("Getting status for contract: contractId={}", contractId);
        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }

        List<SlaMetricSnapshot> metrics = metricRepository.findByContractId(contractId);
        List<SlaCalculationResult> evaluations = slaMonitor.evaluateMetrics(contractId, metrics);

        double overallSlaCompliance = 0.0;
        if (!evaluations.isEmpty()) {
            long passed = evaluations.stream().filter(SlaCalculationResult::isPassed).count();
            overallSlaCompliance = (double) passed / evaluations.size() * 100.0;
        }

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("contractId", contractId);
        status.put("contractName", contract.getContractName());
        status.put("vendorId", contract.getVendorId());
        status.put("regionCode", contract.getRegionCode());
        status.put("status", contract.getStatus());
        status.put("totalMetrics", metrics.size());
        status.put("overallCompliance", String.format("%.2f%%", overallSlaCompliance));
        status.put("evaluations", evaluations);
        status.put("lastChecked", LocalDateTime.now());
        status.put("period", contract.getStartDate() + " to " + contract.getEndDate());
        status.put("daysActive", ChronoUnit.DAYS.between(contract.getStartDate(), java.time.LocalDate.now()));

        return status;
    }

    public Map<String, Object> getMonitoringSummary() {
        log.debug("Generating monitoring summary");
        List<SlaContract> contracts = contractRepository.findAll();
        long totalContracts = contracts.size();
        long activeContracts = contracts.stream()
                .filter(c -> c.getStatus() == VendorSlaStatus.MET)
                .count();
        long breachedContracts = contracts.stream()
                .filter(c -> c.getStatus() == VendorSlaStatus.BREACHED)
                .count();
        long warningContracts = contracts.stream()
                .filter(c -> c.getStatus() == VendorSlaStatus.WARNING)
                .count();
        long pendingContracts = contracts.stream()
                .filter(c -> c.getStatus() == VendorSlaStatus.PENDING)
                .count();

        long totalMetrics = metricRepository.findAll().size();
        double complianceRate = totalContracts > 0
                ? (double) activeContracts / totalContracts * 100.0 : 100.0;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalContracts", totalContracts);
        summary.put("activeContracts", activeContracts);
        summary.put("breachedContracts", breachedContracts);
        summary.put("warningContracts", warningContracts);
        summary.put("pendingContracts", pendingContracts);
        summary.put("totalMetrics", totalMetrics);
        summary.put("complianceRate", String.format("%.2f%%", complianceRate));
        summary.put("lastUpdated", LocalDateTime.now());

        log.info("Monitoring summary: {} total, {} active, {} breached, {} warning",
                totalContracts, activeContracts, breachedContracts, warningContracts);
        return summary;
    }

    public SlaCalculationResult evaluate(String contractId, SlaMetricSnapshot snapshot) {
        log.info("Evaluating metric snapshot for contract: contractId={}, metricType={}, value={}",
                contractId, snapshot.getMetricType(), snapshot.getMetricValue());

        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }

        if (contract.getStatus() != VendorSlaStatus.MET
                && contract.getStatus() != VendorSlaStatus.WARNING) {
            throw new DomainException("CONTRACT_INACTIVE",
                    "Contract is not active: " + contractId);
        }

        metricRepository.save(snapshot);
        log.debug("Metric snapshot saved: snapshotId={}", snapshot.getSnapshotId());

        return slaMonitor.evaluateMetrics(contractId, List.of(snapshot)).stream()
                .findFirst()
                .orElse(null);
    }

    public List<SlaMetricSnapshot> getMetricHistory(String contractId, String metricType) {
        List<SlaMetricSnapshot> metrics = metricRepository.findByContractId(contractId);
        if (metricType != null && !metricType.isBlank()) {
            return metrics.stream()
                    .filter(m -> metricType.equals(m.getMetricType()))
                    .sorted(Comparator.comparing(SlaMetricSnapshot::getTimestamp).reversed())
                    .collect(Collectors.toList());
        }
        return metrics.stream()
                .sorted(Comparator.comparing(SlaMetricSnapshot::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
