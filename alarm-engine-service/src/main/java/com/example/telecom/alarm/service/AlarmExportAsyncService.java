package com.example.telecom.alarm.service;

import com.example.telecom.alarm.dto.AlarmExportRequest;
import com.example.telecom.alarm.dto.AlarmExportResponse;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service for asynchronous alarm export operations. Manages export jobs
 * with status tracking, cancellation, and completion retrieval.
 */
public class AlarmExportAsyncService {

    private final AlarmRepository alarmRepository;
    private final Map<String, ExportJob> exportJobs = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public AlarmExportAsyncService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    /**
     * Initiates an asynchronous alarm export. Returns immediately with a job ID.
     */
    public AlarmExportResponse exportAlarms(AlarmExportRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        String jobId = UUID.randomUUID().toString();
        ExportJob job = new ExportJob(jobId, request, "PENDING");

        exportJobs.put(jobId, job);

        scheduler.submit(() -> processExport(job));

        AlarmExportResponse response = new AlarmExportResponse();
        response.setJobId(jobId);
        response.setStatus("PENDING");
        response.setAlarmCount(0);
        response.setFormat(request.getFormat());
        return response;
    }

    /**
     * Returns the current status of an export job.
     */
    public Optional<AlarmExportResponse> getExportStatus(String jobId) {
        Objects.requireNonNull(jobId, "jobId must not be null");
        ExportJob job = exportJobs.get(jobId);
        if (job == null) {
            return Optional.empty();
        }
        AlarmExportResponse response = new AlarmExportResponse();
        response.setJobId(jobId);
        response.setStatus(job.status);
        response.setAlarmCount(job.processedCount);
        response.setFormat(job.request.getFormat());
        return Optional.of(response);
    }

    /**
     * Attempts to cancel a running export job.
     */
    public boolean cancelExport(String jobId) {
        Objects.requireNonNull(jobId, "jobId must not be null");
        ExportJob job = exportJobs.get(jobId);
        if (job == null) {
            return false;
        }
        if ("PENDING".equals(job.status) || "PROCESSING".equals(job.status)) {
            job.status = "CANCELLED";
            return true;
        }
        return false;
    }

    /**
     * Returns all completed export jobs.
     */
    public List<AlarmExportResponse> getCompletedExports() {
        return exportJobs.values().stream()
                .filter(j -> "COMPLETED".equals(j.status))
                .map(j -> {
                    AlarmExportResponse r = new AlarmExportResponse();
                    r.setJobId(j.jobId);
                    r.setStatus(j.status);
                    r.setAlarmCount(j.processedCount);
                    r.setFormat(j.request.getFormat());
                    return r;
                })
                .collect(Collectors.toList());
    }

    private void processExport(ExportJob job) {
        try {
            job.status = "PROCESSING";

            List<AlarmRecord> alarms = alarmRepository.findAll().stream()
                    .filter(a -> job.request.getRegionCode() == null
                            || job.request.getRegionCode().equals(a.getAlarmRegionCode()))
                    .filter(a -> job.request.getSeverity() == null
                            || job.request.getSeverity().equals(a.getSeverity().name()))
                    .filter(a -> a.getCreatedTime() >= job.request.getStartTime())
                    .filter(a -> job.request.getEndTime() <= 0
                            || a.getCreatedTime() <= job.request.getEndTime())
                    .collect(Collectors.toList());

            Thread.sleep(100);

            job.processedCount = alarms.size();
            job.status = "COMPLETED";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            job.status = "FAILED";
        } catch (Exception e) {
            job.status = "FAILED";
        }
    }

    private static class ExportJob {
        final String jobId;
        final AlarmExportRequest request;
        volatile String status;
        volatile int processedCount;

        ExportJob(String jobId, AlarmExportRequest request, String status) {
            this.jobId = jobId;
            this.request = request;
            this.status = status;
            this.processedCount = 0;
        }
    }
}
