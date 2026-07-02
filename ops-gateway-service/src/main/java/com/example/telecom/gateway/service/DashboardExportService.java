package com.example.telecom.gateway.service;

import com.example.telecom.common.gateway.ExportJob;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DashboardExportService {

    private final Map<String, ExportJob> jobs = new ConcurrentHashMap<>();

    public ExportJob createExportJob(String type, String format, String regionCode) {
        ExportJob job = new ExportJob(UUID.randomUUID().toString(), type, format, "PENDING", regionCode);
        jobs.put(job.getJobId(), job);
        return job;
    }

    public ExportJob startJob(String jobId) {
        ExportJob job = jobs.get(jobId);
        if (job != null) {
            job.setStatus("RUNNING");
        }
        return job;
    }

    public ExportJob completeJob(String jobId) {
        ExportJob job = jobs.get(jobId);
        if (job != null) {
            job.setStatus("COMPLETED");
            job.setCompletedTime(System.currentTimeMillis());
        }
        return job;
    }

    public Optional<ExportJob> findJob(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    public List<ExportJob> findAll() {
        return new ArrayList<>(jobs.values());
    }
}
