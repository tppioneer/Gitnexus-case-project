package com.example.telecom.gateway.service;

import com.example.telecom.gateway.domain.DashboardExportTask;
import com.example.telecom.gateway.dto.DashboardExportRequest;
import com.example.telecom.gateway.dto.DashboardExportResponse;
import com.example.telecom.gateway.repository.DashboardExportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardExportService {

    private final DashboardExportRepository exportRepository;

    public DashboardExportService(DashboardExportRepository exportRepository) {
        this.exportRepository = exportRepository;
    }

    public DashboardExportResponse createExport(DashboardExportRequest request) {
        String exportId = UUID.randomUUID().toString();
        Map<String, String> parameters = new HashMap<>();
        if (request.getScope() != null) {
            parameters.put("scope", request.getScope());
        }
        if (request.getScopeId() != null) {
            parameters.put("scopeId", request.getScopeId());
        }
        if (request.getIncludeCharts() != null) {
            parameters.put("includeCharts", request.getIncludeCharts().toString());
        }
        if (request.getTimeRange() != null) {
            request.getTimeRange().forEach((k, v) -> parameters.put(k, v != null ? v.toString() : null));
        }

        String format = request.getFormat() != null ? request.getFormat() : "PDF";
        String exportType = request.getExportType() != null ? request.getExportType() : "DASHBOARD";

        DashboardExportTask task = new DashboardExportTask(
                exportId, "current-user", exportType, format, "PENDING",
                LocalDateTime.now(), parameters);

        exportRepository.save(task);
        executeExport(task);

        return toResponse(task, 0, null);
    }

    public DashboardExportResponse getExportStatus(String exportId) {
        DashboardExportTask task = exportRepository.findById(exportId)
                .orElseThrow(() -> new RuntimeException("Export task not found: " + exportId));
        int progress = "COMPLETED".equals(task.getStatus()) ? 100 :
                       "PROCESSING".equals(task.getStatus()) ? 50 : 0;
        String downloadUrl = "COMPLETED".equals(task.getStatus())
                ? "/api/dashboard/exports/" + exportId + "/file" : null;
        return toResponse(task, progress, downloadUrl);
    }

    public List<DashboardExportResponse> listExports() {
        List<DashboardExportTask> tasks = exportRepository.findAll();
        return tasks.stream()
                .map(task -> {
                    int progress = "COMPLETED".equals(task.getStatus()) ? 100 :
                                   "PROCESSING".equals(task.getStatus()) ? 50 : 0;
                    String downloadUrl = "COMPLETED".equals(task.getStatus())
                            ? "/api/dashboard/exports/" + task.getExportId() + "/file" : null;
                    return toResponse(task, progress, downloadUrl);
                })
                .collect(Collectors.toList());
    }

    public void cancelExport(String exportId) {
        exportRepository.findById(exportId).ifPresent(task -> {
            task.setStatus("CANCELLED");
            task.setCompletedTime(LocalDateTime.now());
            exportRepository.save(task);
        });
    }

    private void executeExport(DashboardExportTask task) {
        task.setStatus("PROCESSING");
        exportRepository.save(task);

        task.setStatus("COMPLETED");
        task.setCompletedTime(LocalDateTime.now());
        task.setFileSize(1024L);
        exportRepository.save(task);
    }

    private DashboardExportResponse toResponse(DashboardExportTask task, int progress, String downloadUrl) {
        return new DashboardExportResponse(
                task.getExportId(),
                task.getExportType(),
                task.getFormat(),
                task.getStatus(),
                progress,
                task.getCreatedTime(),
                downloadUrl
        );
    }
}
