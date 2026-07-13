package com.example.telecom.sla.service;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.SlaContractRequest;
import com.example.telecom.sla.SlaContractResponse;
import com.example.telecom.sla.mapper.SlaMapper;
import com.example.telecom.sla.repository.SlaContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SlaContractService {

    private static final Logger log = LoggerFactory.getLogger(SlaContractService.class);

    private final SlaContractRepository contractRepository;
    private final SlaMapper mapper;

    public SlaContractService(SlaContractRepository contractRepository, SlaMapper mapper) {
        this.contractRepository = contractRepository;
        this.mapper = mapper;
    }

    public SlaContractResponse createContract(SlaContractRequest request) {
        log.info("Creating new SLA contract: name={}, vendorId={}, region={}",
                request.getContractName(), request.getVendorId(), request.getRegionCode());

        if (request.getStartDate() != null && request.getEndDate() != null) {
            long daysBetween = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
            if (daysBetween < 1) {
                throw new ValidationException("endDate",
                        "Contract period must be at least one day");
            }
            if (daysBefore(request.getStartDate())) {
                throw new ValidationException("startDate",
                        "Start date cannot be in the past");
            }
        }

        SlaContract contract = SlaContract.builder()
                .contractId(UUID.randomUUID().toString())
                .contractName(request.getContractName())
                .vendorId(request.getVendorId())
                .regionCode(request.getRegionCode())
                .responseTimeThreshold(request.getResponseTimeThreshold())
                .resolutionTimeThreshold(request.getResolutionTimeThreshold())
                .availabilityTarget(request.getAvailabilityTarget())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(VendorSlaStatus.PENDING)
                .createdTime(LocalDateTime.now())
                .build();

        validateContract(contract);
        SlaContract saved = contractRepository.save(contract);
        log.info("SLA contract created successfully: contractId={}, status={}",
                saved.getContractId(), saved.getStatus());
        return mapper.toContractResponse(saved);
    }

    public SlaContractResponse getContract(String contractId) {
        log.debug("Fetching SLA contract: contractId={}", contractId);
        SlaContract contract = contractRepository.findById(contractId);
        if (contract == null) {
            log.warn("SLA contract not found: contractId={}", contractId);
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }
        return mapper.toContractResponse(contract);
    }

    public List<SlaContractResponse> listContracts(String status, String vendorId) {
        log.debug("Listing SLA contracts: statusFilter={}, vendorIdFilter={}", status, vendorId);
        List<SlaContract> contracts;

        if (status != null && !status.isBlank() && vendorId != null && !vendorId.isBlank()) {
            contracts = contractRepository.findAll().stream()
                    .filter(c -> status.equalsIgnoreCase(c.getStatus().name()))
                    .filter(c -> vendorId.equals(c.getVendorId()))
                    .collect(Collectors.toList());
        } else if (status != null && !status.isBlank()) {
            VendorSlaStatus slaStatus = VendorSlaStatus.valueOf(status.toUpperCase());
            contracts = contractRepository.findByStatus(slaStatus);
        } else if (vendorId != null && !vendorId.isBlank()) {
            contracts = contractRepository.findByVendorId(vendorId);
        } else {
            contracts = contractRepository.findAll();
        }

        List<SlaContractResponse> responses = contracts.stream()
                .map(mapper::toContractResponse)
                .collect(Collectors.toList());

        log.debug("Found {} contracts matching filters", responses.size());
        return responses;
    }

    public SlaContractResponse updateContract(String contractId, SlaContractRequest request) {
        log.info("Updating SLA contract: contractId={}", contractId);
        SlaContract existing = contractRepository.findById(contractId);
        if (existing == null) {
            log.warn("SLA contract not found for update: contractId={}", contractId);
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }

        if (existing.getStatus() == VendorSlaStatus.BREACHED) {
            throw new DomainException("INVALID_STATUS",
                    "Cannot update a breached contract: " + contractId);
        }

        SlaContract updated = mapper.updateFromRequest(existing, request);
        validateContract(updated);
        SlaContract saved = contractRepository.save(updated);
        log.info("SLA contract updated successfully: contractId={}", contractId);
        return mapper.toContractResponse(saved);
    }

    public void deleteContract(String contractId) {
        log.info("Deleting SLA contract: contractId={}", contractId);
        SlaContract existing = contractRepository.findById(contractId);
        if (existing == null) {
            log.warn("SLA contract not found for deletion: contractId={}", contractId);
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }

        if (existing.getStatus() == VendorSlaStatus.BREACHED) {
            throw new DomainException("INVALID_STATUS",
                    "Cannot delete a breached contract. Resolve breaches first: " + contractId);
        }

        contractRepository.delete(contractId);
        log.info("SLA contract deleted successfully: contractId={}", contractId);
    }

    public SlaContractResponse activateContract(String contractId) {
        log.info("Activating SLA contract: contractId={}", contractId);
        SlaContract existing = contractRepository.findById(contractId);
        if (existing == null) {
            log.warn("SLA contract not found for activation: contractId={}", contractId);
            throw new DomainException("CONTRACT_NOT_FOUND", "SLA contract not found: " + contractId);
        }

        if (existing.getStatus() == VendorSlaStatus.BREACHED) {
            throw new DomainException("INVALID_STATUS",
                    "Cannot activate a breached contract: " + contractId);
        }

        if (existing.getStatus() == VendorSlaStatus.MET) {
            log.info("SLA contract is already active: contractId={}", contractId);
            return mapper.toContractResponse(existing);
        }

        if (existing.getEndDate().isBefore(LocalDate.now())) {
            throw new DomainException("CONTRACT_EXPIRED",
                    "Cannot activate an expired contract: " + contractId);
        }

        SlaContract activated = SlaContract.builder()
                .contractId(existing.getContractId())
                .contractName(existing.getContractName())
                .vendorId(existing.getVendorId())
                .regionCode(existing.getRegionCode())
                .responseTimeThreshold(existing.getResponseTimeThreshold())
                .resolutionTimeThreshold(existing.getResolutionTimeThreshold())
                .availabilityTarget(existing.getAvailabilityTarget())
                .startDate(existing.getStartDate())
                .endDate(existing.getEndDate())
                .status(VendorSlaStatus.MET)
                .createdTime(existing.getCreatedTime())
                .build();

        SlaContract saved = contractRepository.save(activated);
        log.info("SLA contract activated successfully: contractId={}", contractId);
        return mapper.toContractResponse(saved);
    }

    public List<SlaContractResponse> findExpiringContracts(int withinDays) {
        log.debug("Finding contracts expiring within {} days", withinDays);
        LocalDate cutoff = LocalDate.now().plusDays(withinDays);
        List<SlaContract> expiring = contractRepository.findAll().stream()
                .filter(c -> c.getStatus() == VendorSlaStatus.MET)
                .filter(c -> !c.getEndDate().isAfter(cutoff))
                .filter(c -> !c.getEndDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
        return expiring.stream().map(mapper::toContractResponse).collect(Collectors.toList());
    }

    public long countByStatus(VendorSlaStatus status) {
        return contractRepository.findByStatus(status).size();
    }

    private boolean daysBefore(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    private void validateContract(SlaContract contract) {
        List<String> errors = new ArrayList<>();

        if (contract.getContractName() == null || contract.getContractName().isBlank()) {
            errors.add("Contract name must not be empty");
        } else if (contract.getContractName().length() > 200) {
            errors.add("Contract name must not exceed 200 characters");
        }

        if (contract.getVendorId() == null || contract.getVendorId().isBlank()) {
            errors.add("Vendor ID must not be empty");
        }

        if (contract.getRegionCode() == null || contract.getRegionCode().isBlank()) {
            errors.add("Region code must not be empty");
        }

        if (contract.getResponseTimeThreshold() <= 0) {
            errors.add("Response time threshold must be positive");
        } else if (contract.getResponseTimeThreshold() > 86400) {
            errors.add("Response time threshold must not exceed 86400 seconds (24 hours)");
        }

        if (contract.getResolutionTimeThreshold() <= 0) {
            errors.add("Resolution time threshold must be positive");
        } else if (contract.getResolutionTimeThreshold() > 604800) {
            errors.add("Resolution time threshold must not exceed 604800 seconds (7 days)");
        }

        if (contract.getAvailabilityTarget() <= 0 || contract.getAvailabilityTarget() > 100) {
            errors.add("Availability target must be between 0 and 100");
        } else if (contract.getAvailabilityTarget() < 50.0) {
            errors.add("Availability target below 50% is not realistic for an SLA contract");
        }

        if (contract.getStartDate() == null) {
            errors.add("Start date must not be null");
        }

        if (contract.getEndDate() == null) {
            errors.add("End date must not be null");
        }

        if (contract.getStartDate() != null && contract.getEndDate() != null) {
            if (contract.getEndDate().isBefore(contract.getStartDate())) {
                errors.add("End date must be after start date");
            }
            long contractDuration = ChronoUnit.DAYS.between(contract.getStartDate(), contract.getEndDate());
            if (contractDuration > 365 * 5) {
                errors.add("Contract duration must not exceed 5 years");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("contract", String.join("; ", errors));
        }
    }
}
