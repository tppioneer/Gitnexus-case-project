package com.example.telecom.collector.service;

import com.example.telecom.common.audit.AuditEntry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class DeviceImportAuditService {

    private final List<ImportAuditRecord> auditRecords = new ArrayList<>();

    public void recordImport(String batchId, String importType, int totalRecords,
                             int successCount, int failureCount, List<String> errors) {
        ImportAuditRecord record = new ImportAuditRecord(
                batchId,
                importType,
                totalRecords,
                successCount,
                failureCount,
                errors,
                System.currentTimeMillis()
        );
        auditRecords.add(record);
    }

    public List<ImportAuditRecord> getImportHistory() {
        return new ArrayList<>(auditRecords);
    }

    public List<ImportAuditRecord> getImportByDateRange(long startTime, long endTime) {
        return auditRecords.stream()
                .filter(r -> r.getImportTime() >= startTime && r.getImportTime() <= endTime)
                .collect(Collectors.toList());
    }

    public List<ImportAuditRecord> getRecentImports(int limit) {
        return auditRecords.stream()
                .sorted((a, b) -> Long.compare(b.getImportTime(), a.getImportTime()))
                .limit(Math.max(limit, 0))
                .collect(Collectors.toList());
    }

    public ImportStatistics getImportStatistics() {
        int totalImports = auditRecords.size();
        long totalRecords = auditRecords.stream().mapToLong(ImportAuditRecord::getTotalRecords).sum();
        long totalSuccess = auditRecords.stream().mapToLong(ImportAuditRecord::getSuccessCount).sum();
        long totalFailures = auditRecords.stream().mapToLong(ImportAuditRecord::getFailureCount).sum();

        return new ImportStatistics(totalImports, totalRecords, totalSuccess, totalFailures);
    }

    public static class ImportAuditRecord {
        private final String batchId;
        private final String importType;
        private final int totalRecords;
        private final int successCount;
        private final int failureCount;
        private final List<String> errors;
        private final long importTime;

        public ImportAuditRecord(String batchId, String importType, int totalRecords,
                                 int successCount, int failureCount, List<String> errors, long importTime) {
            this.batchId = batchId;
            this.importType = importType;
            this.totalRecords = totalRecords;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
            this.importTime = importTime;
        }

        public String getBatchId() { return batchId; }
        public String getImportType() { return importType; }
        public int getTotalRecords() { return totalRecords; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public List<String> getErrors() { return errors; }
        public long getImportTime() { return importTime; }
    }

    public static class ImportStatistics {
        private final int totalImports;
        private final long totalRecords;
        private final long totalSuccess;
        private final long totalFailures;

        public ImportStatistics(int totalImports, long totalRecords, long totalSuccess, long totalFailures) {
            this.totalImports = totalImports;
            this.totalRecords = totalRecords;
            this.totalSuccess = totalSuccess;
            this.totalFailures = totalFailures;
        }

        public int getTotalImports() { return totalImports; }
        public long getTotalRecords() { return totalRecords; }
        public long getTotalSuccess() { return totalSuccess; }
        public long getTotalFailures() { return totalFailures; }
        public double getSuccessRate() {
            return totalRecords > 0 ? (double) totalSuccess / totalRecords * 100.0 : 0.0;
        }
    }
}
