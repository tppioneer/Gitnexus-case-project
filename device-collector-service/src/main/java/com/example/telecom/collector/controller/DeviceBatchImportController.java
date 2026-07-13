package com.example.telecom.collector.controller;

import com.example.telecom.collector.dto.DeviceBatchImportRequest;
import com.example.telecom.collector.dto.DeviceBatchImportResponse;
import com.example.telecom.collector.dto.DeviceRegistrationRequest;
import com.example.telecom.collector.service.DeviceBatchImportService;
import com.example.telecom.collector.service.DeviceImportAuditService;
import com.example.telecom.collector.service.DeviceImportAuditService.ImportAuditRecord;
import com.example.telecom.collector.validator.DeviceImportValidator;
import com.example.telecom.common.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices/import")
public class DeviceBatchImportController {

    private final DeviceBatchImportService batchImportService;
    private final DeviceImportValidator importValidator;
    private final DeviceImportAuditService auditService;

    private final java.util.concurrent.ConcurrentMap<String, DeviceBatchImportResponse> importJobs = new java.util.concurrent.ConcurrentHashMap<>();

    public DeviceBatchImportController(DeviceBatchImportService batchImportService,
                                        DeviceImportValidator importValidator,
                                        DeviceImportAuditService auditService) {
        this.batchImportService = batchImportService;
        this.importValidator = importValidator;
        this.auditService = auditService;
    }

    @PostMapping("/csv")
    public ApiResponse<DeviceBatchImportResponse> importFromCsv(@RequestBody DeviceBatchImportRequest request) {
        long startTime = System.currentTimeMillis();
        String batchId = UUID.randomUUID().toString();

        List<DeviceRegistrationRequest> devices = parseCsvContent(request.getFileContent());
        List<DeviceRegistrationRequest> validated = importValidator.validateBatch(devices);

        DeviceBatchImportResponse response = new DeviceBatchImportResponse();
        response.setBatchId(batchId);

        com.example.telecom.common.api.OperationResult result = batchImportService.importDevices(validated);

        response.setTotalRecords(validated.size());
        response.setSuccessCount(result.getSuccessCount());
        response.setFailureCount(result.getFailureCount());
        response.setErrors(result.getErrors());
        response.setProcessingTime(System.currentTimeMillis() - startTime);

        importJobs.put(batchId, response);

        auditService.recordImport(batchId, "CSV", validated.size(),
                result.getSuccessCount(), result.getFailureCount(), result.getErrors());

        return ApiResponse.success(response);
    }

    @PostMapping("/json")
    public ApiResponse<DeviceBatchImportResponse> importFromJson(@RequestBody DeviceBatchImportRequest request) {
        long startTime = System.currentTimeMillis();
        String batchId = UUID.randomUUID().toString();

        List<DeviceRegistrationRequest> devices = parseJsonContent(request.getFileContent());
        List<DeviceRegistrationRequest> validated = importValidator.validateBatch(devices);

        DeviceBatchImportResponse response = new DeviceBatchImportResponse();
        response.setBatchId(batchId);

        com.example.telecom.common.api.OperationResult result = batchImportService.importDevices(validated);

        response.setTotalRecords(validated.size());
        response.setSuccessCount(result.getSuccessCount());
        response.setFailureCount(result.getFailureCount());
        response.setErrors(result.getErrors());
        response.setProcessingTime(System.currentTimeMillis() - startTime);

        importJobs.put(batchId, response);

        auditService.recordImport(batchId, "JSON", validated.size(),
                result.getSuccessCount(), result.getFailureCount(), result.getErrors());

        return ApiResponse.success(response);
    }

    @GetMapping("/jobs/{jobId}")
    public ApiResponse<DeviceBatchImportResponse> getImportJob(@PathVariable String jobId) {
        DeviceBatchImportResponse job = importJobs.get(jobId);
        if (job == null) {
            return ApiResponse.error(404, "Import job not found: " + jobId);
        }
        return ApiResponse.success(job);
    }

    @GetMapping("/jobs")
    public ApiResponse<List<DeviceBatchImportResponse>> listImportJobs() {
        return ApiResponse.success(new ArrayList<>(importJobs.values()));
    }

    @GetMapping("/audit")
    public ApiResponse<List<ImportAuditRecord>> getImportAuditTrail() {
        return ApiResponse.success(auditService.getImportHistory());
    }

    private List<DeviceRegistrationRequest> parseCsvContent(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Collections.emptyList();
        }
        return lines.stream()
                .skip(1)
                .filter(line -> line != null && !line.isBlank())
                .map(line -> {
                    String[] columns = line.split(",");
                    DeviceRegistrationRequest request = new DeviceRegistrationRequest();
                    if (columns.length > 0) request.setDeviceName(columns[0].trim());
                    if (columns.length > 1) request.setVendor(columns[1].trim());
                    if (columns.length > 2) request.setRegionCode(columns[2].trim());
                    if (columns.length > 3) request.setSiteCode(columns[3].trim());
                    if (columns.length > 4) request.setManagementIp(columns[4].trim());
                    return request;
                })
                .collect(Collectors.toList());
    }

    private List<DeviceRegistrationRequest> parseJsonContent(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Collections.emptyList();
        }
        List<DeviceRegistrationRequest> requests = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) continue;
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(line);
                DeviceRegistrationRequest request = new DeviceRegistrationRequest();
                if (node.has("deviceName")) request.setDeviceName(node.get("deviceName").asText());
                if (node.has("vendor")) request.setVendor(node.get("vendor").asText());
                if (node.has("regionCode")) request.setRegionCode(node.get("regionCode").asText());
                if (node.has("siteCode")) request.setSiteCode(node.get("siteCode").asText());
                if (node.has("managementIp")) request.setManagementIp(node.get("managementIp").asText());
                requests.add(request);
            } catch (Exception e) {
                // Skip malformed JSON lines
            }
        }
        return requests;
    }
}
