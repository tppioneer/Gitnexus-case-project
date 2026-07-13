package com.example.telecom.gateway.evaluator;

import com.example.telecom.gateway.client.AlarmClient;
import com.example.telecom.common.alarm.AlarmRecord;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ServiceHealthEvaluator {

    private final AlarmClient alarmClient;

    private static final Map<String, List<String>> SERVICE_DEPENDENCIES = new LinkedHashMap<>();

    static {
        SERVICE_DEPENDENCIES.put("api-gateway", Arrays.asList("authentication", "rate-limiter", "load-balancer"));
        SERVICE_DEPENDENCIES.put("device-collector", Arrays.asList("message-queue", "database", "api-gateway"));
        SERVICE_DEPENDENCIES.put("alarm-engine", Arrays.asList("device-collector", "database", "message-queue"));
        SERVICE_DEPENDENCIES.put("workorder-service", Arrays.asList("alarm-engine", "database", "api-gateway"));
        SERVICE_DEPENDENCIES.put("notification", Arrays.asList("message-queue", "api-gateway"));
        SERVICE_DEPENDENCIES.put("monitoring", Arrays.asList("device-collector", "alarm-engine"));
    }

    public ServiceHealthEvaluator(AlarmClient alarmClient) {
        this.alarmClient = alarmClient;
    }

    public Map<String, Object> evaluate(String serviceName) {
        Map<String, Object> result = new HashMap<>();
        result.put("serviceName", serviceName);
        result.put("status", "active");

        List<AlarmRecord> alarms = alarmClient.getAllAlarms();
        long criticalAlarms = alarms.stream()
                .filter(a -> a.getSeverity() != null && a.getSeverity().name().equalsIgnoreCase("CRITICAL"))
                .count();
        long warningAlarms = alarms.stream()
                .filter(a -> a.getSeverity() != null && a.getSeverity().name().equalsIgnoreCase("WARNING"))
                .count();

        double availabilityScore = calculateAvailabilityScore(criticalAlarms, warningAlarms);
        double overallScore = availabilityScore;

        result.put("overallScore", Math.round(overallScore * 100.0) / 100.0);
        result.put("healthLevel", getHealthLevel(overallScore));
        result.put("criticalAlarms", criticalAlarms);
        result.put("warningAlarms", warningAlarms);
        result.put("totalAlarms", alarms.size());
        result.put("dependencies", getServiceDependencies(serviceName));

        return result;
    }

    public List<Map<String, Object>> evaluateAllServices() {
        List<Map<String, Object>> results = new ArrayList<>();
        for (String serviceName : SERVICE_DEPENDENCIES.keySet()) {
            results.add(evaluate(serviceName));
        }
        return results;
    }

    public List<String> getServiceDependencies(String serviceName) {
        return SERVICE_DEPENDENCIES.getOrDefault(serviceName, Collections.emptyList());
    }

    private double calculateAvailabilityScore(long criticalAlarms, long warningAlarms) {
        double penalty = criticalAlarms * 15.0 + warningAlarms * 5.0;
        return Math.max(0.0, 100.0 - penalty);
    }

    private String getHealthLevel(double score) {
        if (score >= 80.0) return "healthy";
        else if (score >= 60.0) return "fair";
        else if (score >= 40.0) return "degraded";
        else return "critical";
    }
}
