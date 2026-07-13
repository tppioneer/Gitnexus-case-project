package com.example.telecom.alarm.service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service for scheduling alarm report generation. Supports scheduling
 * reports at intervals, listing schedules, cancelling them, and
 * executing due reports on demand.
 */
public class AlarmReportScheduler {

    private final AlarmSummaryReportGenerator reportGenerator;
    private final Map<String, ScheduledReport> schedules = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    public AlarmReportScheduler(AlarmSummaryReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    /**
     * Schedules a recurring report with the given ID and interval in minutes.
     */
    public ScheduledReport scheduleReport(String reportId, String period, long intervalMinutes) {
        Objects.requireNonNull(reportId, "reportId must not be null");
        Objects.requireNonNull(period, "period must not be null");

        ScheduledReport report = new ScheduledReport(reportId, period, intervalMinutes, "ACTIVE");
        schedules.put(reportId, report);

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
                () -> executeReport(reportId),
                0,
                intervalMinutes,
                TimeUnit.MINUTES
        );
        report.future = future;
        return report;
    }

    /**
     * Cancels a scheduled report.
     */
    public boolean cancelSchedule(String reportId) {
        Objects.requireNonNull(reportId, "reportId must not be null");
        ScheduledReport report = schedules.get(reportId);
        if (report == null) {
            return false;
        }
        if (report.future != null && !report.future.isDone()) {
            report.future.cancel(false);
        }
        report.status = "CANCELLED";
        return true;
    }

    /**
     * Lists all registered report schedules.
     */
    public List<ScheduledReport> listSchedules() {
        return new ArrayList<>(schedules.values());
    }

    /**
     * Executes all reports that are due (based on their interval).
     * Returns a list of generated report strings.
     */
    public List<String> executeDueReports() {
        long now = System.currentTimeMillis();
        List<String> results = new ArrayList<>();

        for (ScheduledReport report : schedules.values()) {
            if (!"ACTIVE".equals(report.status)) {
                continue;
            }
            if (now - report.lastExecuted >= report.intervalMinutes * 60 * 1000) {
                String result = executeReport(report.reportId);
                results.add(result);
            }
        }
        return results;
    }

    private String executeReport(String reportId) {
        ScheduledReport report = schedules.get(reportId);
        if (report == null) {
            return "Report not found: " + reportId;
        }
        try {
            String result = reportGenerator.generate(report.period);
            report.lastExecuted = System.currentTimeMillis();
            report.lastResult = result;
            return result;
        } catch (Exception e) {
            String error = "Failed to generate report " + reportId + ": " + e.getMessage();
            report.lastResult = error;
            return error;
        }
    }

    /**
     * Represents a scheduled report with its configuration and status.
     */
    public static class ScheduledReport {
        private final String reportId;
        private final String period;
        private final long intervalMinutes;
        private volatile String status;
        private volatile long lastExecuted;
        private volatile String lastResult;
        private ScheduledFuture<?> future;

        public ScheduledReport(String reportId, String period, long intervalMinutes, String status) {
            this.reportId = reportId;
            this.period = period;
            this.intervalMinutes = intervalMinutes;
            this.status = status;
            this.lastExecuted = 0;
            this.lastResult = null;
        }

        public String getReportId() { return reportId; }
        public String getPeriod() { return period; }
        public long getIntervalMinutes() { return intervalMinutes; }
        public String getStatus() { return status; }
        public long getLastExecuted() { return lastExecuted; }
        public String getLastResult() { return lastResult; }
    }
}
