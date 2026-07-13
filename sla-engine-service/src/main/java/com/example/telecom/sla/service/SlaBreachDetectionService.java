package com.example.telecom.sla.service;

import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.domain.SlaBreach;
import com.example.telecom.sla.event.SlaEventPublisher;
import com.example.telecom.sla.monitor.SlaBreachPolicy;
import com.example.telecom.sla.repository.SlaBreachRepository;
import com.example.telecom.sla.repository.SlaContractRepository;
import com.example.telecom.sla.repository.SlaMetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlaBreachDetectionService {

    private static final Logger log = LoggerFactory.getLogger(SlaBreachDetectionService.class);

    private final SlaContractRepository contractRepository;
    private final SlaMetricRepository metricRepository;
    private final SlaBreachRepository breachRepository;
    private final SlaBreachPolicy breachPolicy;
    private final SlaEventPublisher eventPublisher;

    public SlaBreachDetectionService(SlaContractRepository contractRepository,
                                     SlaMetricRepository metricRepository,
                                     SlaBreachRepository breachRepository,
                                     SlaBreachPolicy breachPolicy,
                                     SlaEventPublisher eventPublisher) {
        this.contractRepository = contractRepository;
        this.metricRepository = metricRepository;
        this.breachRepository = breachRepository;
        this.breachPolicy = breachPolicy;
        this.eventPublisher = eventPublisher;
    }

    public List<SlaBreach> detect(String contractId) {
        log.info("Running breach detection for contract: contractId={}", contractId);
        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            log.warn("SLA contract not found for breach detection: contractId={}", contractId);
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }

        if (contract.getStatus() == VendorSlaStatus.PENDING) {
            log.info("Contract is PENDING, skipping breach detection: contractId={}", contractId);
            return new ArrayList<>();
        }

        List<SlaMetricSnapshot> metrics = metricRepository.findByContractId(contractId);
        if (metrics.isEmpty()) {
            log.info("No metrics found for contract, skipping breach detection: contractId={}", contractId);
            return new ArrayList<>();
        }

        List<SlaBreach> detectedBreaches = new ArrayList<>();
        List<SlaBreach> existingBreaches = breachRepository.findByContractId(contractId);
        Set<String> existingBreachMetrics = existingBreaches.stream()
                .filter(b -> b.getStatus() == VendorSlaStatus.BREACHED)
                .map(SlaBreach::getMetricType)
                .collect(Collectors.toSet());

        for (SlaMetricSnapshot metric : metrics) {
            if (existingBreachMetrics.contains(metric.getMetricType())) {
                log.debug("Active breach already exists for metric type {}, skipping", metric.getMetricType());
                continue;
            }
            SlaBreach breach = checkBreach(contract, metric);
            if (breach != null) {
                detectedBreaches.add(breach);
            }
        }

        for (SlaBreach breach : detectedBreaches) {
            breachRepository.save(breach);
            eventPublisher.publishBreachDetected(breach);
            log.warn("SLA breach detected and saved: contractId={}, metricType={}, value={}, threshold={}",
                    contractId, breach.getMetricType(), breach.getActualValue(), breach.getThreshold());
        }

        if (!detectedBreaches.isEmpty()) {
            SlaContract updated = SlaContract.builder()
                    .contractId(contract.getContractId())
                    .contractName(contract.getContractName())
                    .vendorId(contract.getVendorId())
                    .regionCode(contract.getRegionCode())
                    .responseTimeThreshold(contract.getResponseTimeThreshold())
                    .resolutionTimeThreshold(contract.getResolutionTimeThreshold())
                    .availabilityTarget(contract.getAvailabilityTarget())
                    .startDate(contract.getStartDate())
                    .endDate(contract.getEndDate())
                    .status(VendorSlaStatus.BREACHED)
                    .createdTime(contract.getCreatedTime())
                    .build();
            contractRepository.save(updated);
            log.warn("Contract status updated to BREACHED: contractId={}", contractId);
        }

        log.info("Breach detection complete for contractId={}: {} new breaches detected",
                contractId, detectedBreaches.size());
        return detectedBreaches;
    }

    public SlaBreach checkBreach(SlaContract contract, SlaMetricSnapshot snapshot) {
        if (breachPolicy.evaluate(contract, snapshot)) {
            Severity severity = breachPolicy.getBreachSeverity(contract, snapshot);
            String description = breachPolicy.getBreachDescription(contract, snapshot);

            SlaBreach breach = SlaBreach.builder()
                    .breachId(UUID.randomUUID().toString())
                    .contractId(contract.getContractId())
                    .metricType(snapshot.getMetricType())
                    .actualValue(snapshot.getMetricValue())
                    .threshold(getThresholdForMetric(contract, snapshot))
                    .severity(severity)
                    .status(VendorSlaStatus.BREACHED)
                    .detectedTime(LocalDateTime.now())
                    .build();

            log.debug("Breach condition met: contractId={}, metricType={}, severity={}, description={}",
                    contract.getContractId(), snapshot.getMetricType(), severity, description);
            return breach;
        }
        return null;
    }

    public List<SlaBreach> getBreachHistory(String contractId) {
        log.debug("Fetching breach history for contract: contractId={}", contractId);
        List<SlaBreach> breaches = breachRepository.findByContractId(contractId);
        return breaches.stream()
                .sorted(Comparator.comparing(SlaBreach::getDetectedTime).reversed())
                .collect(Collectors.toList());
    }

    public List<SlaBreach> getActiveBreaches() {
        log.debug("Fetching all active breaches");
        return breachRepository.findActiveBreaches();
    }

    public Map<String, Long> getBreachSummary() {
        List<SlaBreach> allBreaches = breachRepository.findAll();
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("total", (long) allBreaches.size());
        summary.put("active", allBreaches.stream()
                .filter(b -> b.getStatus() == VendorSlaStatus.BREACHED).count());
        summary.put("resolved", allBreaches.stream()
                .filter(b -> b.getStatus() == VendorSlaStatus.MET).count());
        summary.put("critical", allBreaches.stream()
                .filter(b -> b.getSeverity() == Severity.CRITICAL).count());
        summary.put("major", allBreaches.stream()
                .filter(b -> b.getSeverity() == Severity.MAJOR).count());
        summary.put("warning", allBreaches.stream()
                .filter(b -> b.getSeverity() == Severity.WARNING).count());
        return summary;
    }

    public SlaBreach resolveBreach(String breachId) {
        log.info("Resolving SLA breach: breachId={}", breachId);
        SlaBreach breach = breachRepository.findById(breachId);
        if (breach == null) {
            throw new DomainException("BREACH_NOT_FOUND", "SLA breach not found: " + breachId);
        }

        if (breach.getStatus() == VendorSlaStatus.MET) {
            log.info("Breach is already resolved: breachId={}", breachId);
            return breach;
        }

        breach.setStatus(VendorSlaStatus.MET);
        breach.setResolvedTime(LocalDateTime.now());
        breachRepository.save(breach);
        eventPublisher.publishBreachResolved(breach);

        String contractId = breach.getContractId();
        List<SlaBreach> activeBreaches = breachRepository.findByContractId(contractId).stream()
                .filter(b -> b.getStatus() == VendorSlaStatus.BREACHED)
                .collect(Collectors.toList());

        if (activeBreaches.isEmpty()) {
            SlaContract contract = contractRepository.findById(contractId);
            if (contract != null) {
                SlaContract updated = SlaContract.builder()
                        .contractId(contract.getContractId())
                        .contractName(contract.getContractName())
                        .vendorId(contract.getVendorId())
                        .regionCode(contract.getRegionCode())
                        .responseTimeThreshold(contract.getResponseTimeThreshold())
                        .resolutionTimeThreshold(contract.getResolutionTimeThreshold())
                        .availabilityTarget(contract.getAvailabilityTarget())
                        .startDate(contract.getStartDate())
                        .endDate(contract.getEndDate())
                        .status(VendorSlaStatus.MET)
                        .createdTime(contract.getCreatedTime())
                        .build();
                contractRepository.save(updated);
                log.info("Contract status restored to MET as all breaches resolved: contractId={}", contractId);
            }
        }

        log.info("SLA breach resolved successfully: breachId={}", breachId);
        return breach;
    }

    private double getThresholdForMetric(SlaContract contract, SlaMetricSnapshot snapshot) {
        switch (snapshot.getMetricType()) {
            case "response_time":
                return contract.getResponseTimeThreshold();
            case "resolution_time":
                return contract.getResolutionTimeThreshold();
            case "availability":
                return contract.getAvailabilityTarget();
            default:
                return snapshot.getThreshold();
        }
    }
}
