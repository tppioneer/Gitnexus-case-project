package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.FederatedAlarmSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class FederatedAlarmExportService {

    private final ObjectMapper objectMapper;
    private final ConcurrentLinkedDeque<ExportRecord> exportHistory = new ConcurrentLinkedDeque<>();

    public FederatedAlarmExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public String exportToCsv(List<FederatedAlarmRecord> alarms) {
        StringBuilder sb = new StringBuilder();
        sb.append("AlarmId,SourceId,DeviceId,AlarmType,Severity,AlarmTime,RegionCode,Status,Description\n");

        for (FederatedAlarmRecord alarm : alarms) {
            sb.append(escapeCsv(alarm.getAlarmId())).append(",");
            sb.append(escapeCsv(alarm.getSourceId())).append(",");
            sb.append(escapeCsv(alarm.getDeviceId())).append(",");
            sb.append(escapeCsv(alarm.getAlarmType())).append(",");
            sb.append(escapeCsv(alarm.getSeverity() != null ? alarm.getSeverity().name() : "")).append(",");
            sb.append(escapeCsv(alarm.getAlarmTime() != null ? alarm.getAlarmTime().toString() : "")).append(",");
            sb.append(escapeCsv(alarm.getRegionCode())).append(",");
            sb.append(escapeCsv(alarm.getStatus() != null ? alarm.getStatus().name() : "")).append(",");
            sb.append(escapeCsv(alarm.getDescription())).append("\n");
        }

        recordExport("CSV", alarms.size());
        return sb.toString();
    }

    public String exportToJson(List<FederatedAlarmRecord> alarms) {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(alarms);
            recordExport("JSON", alarms.size());
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to export alarms to JSON", e);
        }
    }

    public List<ExportRecord> getExportHistory() {
        return new ArrayList<>(exportHistory);
    }

    public ExportRecord scheduleExport(FederatedAlarmSummary summary) {
        ExportRecord record = new ExportRecord(
                UUID.randomUUID().toString(),
                "SCHEDULED",
                summary.getTotalCount(),
                LocalDateTime.now(),
                "PENDING"
        );
        exportHistory.addFirst(record);
        return record;
    }

    private void recordExport(String format, long recordCount) {
        ExportRecord record = new ExportRecord(
                UUID.randomUUID().toString(),
                format,
                recordCount,
                LocalDateTime.now(),
                "COMPLETED"
        );
        exportHistory.addFirst(record);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public static class ExportRecord {
        private final String id;
        private final String format;
        private final long recordCount;
        private final LocalDateTime exportedAt;
        private final String status;

        public ExportRecord(String id, String format, long recordCount, LocalDateTime exportedAt, String status) {
            this.id = id;
            this.format = format;
            this.recordCount = recordCount;
            this.exportedAt = exportedAt;
            this.status = status;
        }

        public String getId() { return id; }
        public String getFormat() { return format; }
        public long getRecordCount() { return recordCount; }
        public LocalDateTime getExportedAt() { return exportedAt; }
        public String getStatus() { return status; }
    }
}
