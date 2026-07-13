package com.example.telecom.sla.mapper;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.SlaBreachResponse;
import com.example.telecom.sla.SlaContractRequest;
import com.example.telecom.sla.SlaContractResponse;
import com.example.telecom.sla.SlaReportResponse;
import com.example.telecom.sla.domain.SlaBreach;
import com.example.telecom.sla.domain.SlaReport;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class SlaMapper {

    public SlaContractResponse toContractResponse(SlaContract contract) {
        if (contract == null) {
            return null;
        }
        SlaContractResponse response = new SlaContractResponse();
        response.setContractId(contract.getContractId());
        response.setContractName(contract.getContractName());
        response.setVendorId(contract.getVendorId());
        response.setRegionCode(contract.getRegionCode());
        response.setResponseTimeThreshold(contract.getResponseTimeThreshold());
        response.setResolutionTimeThreshold(contract.getResolutionTimeThreshold());
        response.setAvailabilityTarget(contract.getAvailabilityTarget());
        response.setStartDate(contract.getStartDate());
        response.setEndDate(contract.getEndDate());
        response.setStatus(contract.getStatus());
        response.setCreatedTime(contract.getCreatedTime());
        return response;
    }

    public SlaBreachResponse toBreachResponse(SlaBreach breach) {
        if (breach == null) {
            return null;
        }
        SlaBreachResponse response = new SlaBreachResponse();
        response.setBreachId(breach.getBreachId());
        response.setContractId(breach.getContractId());
        response.setMetricType(breach.getMetricType());
        response.setActualValue(breach.getActualValue());
        response.setThreshold(breach.getThreshold());
        response.setSeverity(breach.getSeverity());
        response.setStatus(breach.getStatus());
        response.setDetectedTime(breach.getDetectedTime());
        response.setResolvedTime(breach.getResolvedTime());
        response.setEscalatedTo(breach.getEscalatedTo());
        return response;
    }

    public SlaReportResponse toReportResponse(SlaReport report) {
        if (report == null) {
            return null;
        }
        SlaReportResponse response = new SlaReportResponse();
        response.setReportId(report.getReportId());
        response.setContractId(report.getContractId());
        response.setReportType(report.getReportType());
        response.setPeriodStart(report.getPeriodStart());
        response.setPeriodEnd(report.getPeriodEnd());
        response.setSlaPercentage(report.getSlaPercentage());
        response.setMetrics(report.getMetrics());
        response.setGeneratedTime(report.getGeneratedTime());
        response.setStatus(report.getStatus());
        response.setFormat(report.getFormat());
        return response;
    }

    public SlaMetricSnapshot toSnapshot(String snapshotId, String contractId,
                                         String metricType, double metricValue,
                                         double threshold, LocalDateTime timestamp,
                                         VendorSlaStatus status) {
        return new SlaMetricSnapshot(snapshotId, contractId, metricType,
                metricValue, threshold, timestamp, status);
    }

    public SlaContract toContract(SlaContractRequest request, VendorSlaStatus status) {
        return SlaContract.builder()
                .contractId(UUID.randomUUID().toString())
                .contractName(request.getContractName())
                .vendorId(request.getVendorId())
                .regionCode(request.getRegionCode())
                .responseTimeThreshold(request.getResponseTimeThreshold())
                .resolutionTimeThreshold(request.getResolutionTimeThreshold())
                .availabilityTarget(request.getAvailabilityTarget())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(status)
                .createdTime(LocalDateTime.now())
                .build();
    }

    public SlaContract updateFromRequest(SlaContract existing, SlaContractRequest request) {
        String contractName = request.getContractName() != null
                ? request.getContractName() : existing.getContractName();
        String vendorId = request.getVendorId() != null
                ? request.getVendorId() : existing.getVendorId();
        String regionCode = request.getRegionCode() != null
                ? request.getRegionCode() : existing.getRegionCode();
        int responseTimeThreshold = request.getResponseTimeThreshold() > 0
                ? request.getResponseTimeThreshold() : existing.getResponseTimeThreshold();
        int resolutionTimeThreshold = request.getResolutionTimeThreshold() > 0
                ? request.getResolutionTimeThreshold() : existing.getResolutionTimeThreshold();
        double availabilityTarget = request.getAvailabilityTarget() > 0
                ? request.getAvailabilityTarget() : existing.getAvailabilityTarget();
        var startDate = request.getStartDate() != null
                ? request.getStartDate() : existing.getStartDate();
        var endDate = request.getEndDate() != null
                ? request.getEndDate() : existing.getEndDate();

        return SlaContract.builder()
                .contractId(existing.getContractId())
                .contractName(contractName)
                .vendorId(vendorId)
                .regionCode(regionCode)
                .responseTimeThreshold(responseTimeThreshold)
                .resolutionTimeThreshold(resolutionTimeThreshold)
                .availabilityTarget(availabilityTarget)
                .startDate(startDate)
                .endDate(endDate)
                .status(existing.getStatus())
                .createdTime(existing.getCreatedTime())
                .build();
    }
}
