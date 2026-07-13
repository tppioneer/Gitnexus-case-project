package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.FederatedAlarmSource;
import com.example.telecom.alarm.federated.FederatedAlarmSourceRegistry;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FederatedHealthCheckService {

    private final FederatedAlarmRepository alarmRepository;
    private final FederatedAlarmSourceRegistry sourceRegistry;

    private final AtomicLong totalAlarmsIngested = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    private volatile LocalDateTime lastIngestTime;

    public FederatedHealthCheckService(FederatedAlarmRepository alarmRepository,
                                        FederatedAlarmSourceRegistry sourceRegistry) {
        this.alarmRepository = alarmRepository;
        this.sourceRegistry = sourceRegistry;
    }

    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("totalAlarmsInStore", alarmRepository.count());
        health.put("totalAlarmsIngested", totalAlarmsIngested.get());
        health.put("lastIngestTime", lastIngestTime != null ? lastIngestTime.toString() : "N/A");
        health.put("errorCount", errorCount.get());
        health.put("registeredSources", sourceRegistry.getAllSources().size());
        health.put("timestamp", LocalDateTime.now().toString());
        return health;
    }

    public Map<String, Object> checkSourceConnectivity(String sourceId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sourceId", sourceId);

        java.util.Optional<FederatedAlarmSource> sourceOpt = sourceRegistry.resolve(sourceId);
        if (sourceOpt.isPresent()) {
            FederatedAlarmSource source = sourceOpt.get();
            result.put("status", source.getStatus());
            result.put("region", source.getRegionCode());
            result.put("lastHeartbeat", source.getLastHeartbeat() != null
                    ? source.getLastHeartbeat().toString() : "N/A");
            result.put("reachable", "ACTIVE".equals(source.getStatus()));
        } else {
            result.put("status", "UNKNOWN");
            result.put("reachable", false);
        }

        return result;
    }

    public LocalDateTime getLastIngestTime() {
        return lastIngestTime;
    }

    public void recordIngest() {
        totalAlarmsIngested.incrementAndGet();
        lastIngestTime = LocalDateTime.now();
    }

    public long getTotalAlarmsIngested() {
        return totalAlarmsIngested.get();
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public void recordError() {
        errorCount.incrementAndGet();
    }
}
