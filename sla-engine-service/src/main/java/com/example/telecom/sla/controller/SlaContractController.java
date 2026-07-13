package com.example.telecom.sla.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.sla.SlaContractRequest;
import com.example.telecom.sla.SlaContractResponse;
import com.example.telecom.sla.service.SlaContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sla/contracts")
public class SlaContractController {

    private static final Logger log = LoggerFactory.getLogger(SlaContractController.class);

    private final SlaContractService contractService;

    public SlaContractController(SlaContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SlaContractResponse>> create(@RequestBody SlaContractRequest request) {
        log.info("POST /api/sla/contracts - creating contract: name={}, vendorId={}",
                request.getContractName(), request.getVendorId());
        try {
            SlaContractResponse response = contractService.createContract(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
        } catch (ValidationException e) {
            log.warn("Validation error creating contract: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (DomainException e) {
            log.warn("Domain error creating contract: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, e.getMessage()));
        }
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ApiResponse<SlaContractResponse>> get(@PathVariable String contractId) {
        log.info("GET /api/sla/contracts/{}", contractId);
        try {
            SlaContractResponse response = contractService.getContract(contractId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DomainException e) {
            log.warn("Contract not found: {}", contractId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SlaContractResponse>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String vendorId) {
        log.info("GET /api/sla/contracts - status={}, vendorId={}", status, vendorId);
        List<SlaContractResponse> contracts = contractService.listContracts(status, vendorId);
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }

    @PutMapping("/{contractId}")
    public ResponseEntity<ApiResponse<SlaContractResponse>> update(
            @PathVariable String contractId,
            @RequestBody SlaContractRequest request) {
        log.info("PUT /api/sla/contracts/{}", contractId);
        try {
            SlaContractResponse response = contractService.updateContract(contractId, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (ValidationException e) {
            log.warn("Validation error updating contract: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (DomainException e) {
            log.warn("Error updating contract: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @DeleteMapping("/{contractId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String contractId) {
        log.info("DELETE /api/sla/contracts/{}", contractId);
        try {
            contractService.deleteContract(contractId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (DomainException e) {
            log.warn("Error deleting contract: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @PostMapping("/{contractId}/activate")
    public ResponseEntity<ApiResponse<SlaContractResponse>> activate(@PathVariable String contractId) {
        log.info("POST /api/sla/contracts/{}/activate", contractId);
        try {
            SlaContractResponse response = contractService.activateContract(contractId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (DomainException e) {
            log.warn("Error activating contract: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        }
    }
}
