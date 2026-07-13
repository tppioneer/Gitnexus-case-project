package com.example.telecom.gateway.service;

import com.example.telecom.gateway.client.AlarmClient;
import com.example.telecom.gateway.client.DeviceClient;
import com.example.telecom.gateway.client.WorkOrderClient;
import com.example.telecom.gateway.dto.DashboardHealthScoreResponse;
import com.example.telecom.gateway.evaluator.DeviceHealthEvaluator;
import com.example.telecom.gateway.evaluator.RegionHealthEvaluator;
import com.example.telecom.gateway.evaluator.ServiceHealthEvaluator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DashboardHealthScoreService {

    private final DeviceClient deviceClient;
    private final AlarmClient alarmClient;
    private final WorkOrderClient workOrderClient;
    private final DeviceHealthEvaluator deviceHealthEvaluator;
    private final RegionHealthEvaluator regionHealthEvaluator;
    private final ServiceHealthEvaluator serviceHealthEvaluator;

    public DashboardHealthScoreService(DeviceClient deviceClient, AlarmClient alarmClient,
                                       WorkOrderClient workOrderClient,
                                       DeviceHealthEvaluator deviceHealthEvaluator,
                                       RegionHealthEvaluator regionHealthEvaluator,
                                       ServiceHealthEvaluator serviceHealthEvaluator) {
        this.deviceClient = deviceClient;
        this.alarmClient = alarmClient;
        this.workOrderClient = workOrderClient;
        this.deviceHealthEvaluator = deviceHealthEvaluator;
        this.regionHealthEvaluator = regionHealthEvaluator;
        this.serviceHealthEvaluator = serviceHealthEvaluator;
    }

    public DashboardHealthScoreResponse calculateOverallScore() {
        Map<String, Object> deviceEval = deviceHealthEvaluator.evaluate("overall");
        Map<String, Object> regionEval = regionHealthEvaluator.evaluate("overall");
        Map<String, Object> serviceEval = serviceHealthEvaluator.evaluate("api-gateway");

        double deviceScore = (Double) deviceEval.getOrDefault("overallHealth", 85.0);
        double regionScore = (Double) regionEval.getOrDefault("overallScore", 80.0);
        double serviceScore = (Double) serviceEval.getOrDefault("overallScore", 90.0);

        Map<String, Double> scores = new HashMap<>();
        scores.put("device", deviceScore);
        scores.put("alarm", regionScore);
        scores.put("service", serviceScore);

        Map<String, Double> weights = new HashMap<>();
        weights.put("device", 0.4);
        weights.put("alarm", 0.35);
        weights.put("service", 0.25);

        double overall = calculateWeightedScore(scores, weights);

        return new DashboardHealthScoreResponse(
                "overall", "all", overall, deviceScore, regionScore,
                serviceScore, LocalDateTime.now(), getScoreHistory("overall", 7));
    }

    public DashboardHealthScoreResponse calculateRegionScore(String regionCode) {
        Double regionScore = regionHealthEvaluator.getRegionScore(regionCode);
        Map<String, Object> deviceEval = deviceHealthEvaluator.evaluate(regionCode);

        double deviceScore = (Double) deviceEval.getOrDefault("overallHealth", 85.0);

        return new DashboardHealthScoreResponse(
                "region", regionCode, regionScore, deviceScore, regionScore,
                85.0, LocalDateTime.now(), getScoreHistory("region:" + regionCode, 7));
    }

    public DashboardHealthScoreResponse calculateDeviceScore(String deviceId) {
        Map<String, Object> deviceEval = deviceHealthEvaluator.evaluate(deviceId);
        double deviceScore = (Double) deviceEval.getOrDefault("overallHealth", 85.0);

        return new DashboardHealthScoreResponse(
                "device", deviceId, deviceScore, deviceScore, 90.0,
                85.0, LocalDateTime.now(), getScoreHistory("device:" + deviceId, 7));
    }

    public DashboardHealthScoreResponse calculateServiceScore(String serviceName) {
        Map<String, Object> serviceEval = serviceHealthEvaluator.evaluate(serviceName);
        double serviceScore = (Double) serviceEval.getOrDefault("overallScore", 90.0);

        return new DashboardHealthScoreResponse(
                "service", serviceName, serviceScore, 85.0, 90.0,
                serviceScore, LocalDateTime.now(), getScoreHistory("service:" + serviceName, 7));
    }

    public List<Map<String, Object>> getScoreHistory(String scope, int days) {
        List<Map<String, Object>> history = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("timestamp", now.minusDays(i));
            entry.put("score", 70.0 + Math.random() * 30.0);
            entry.put("scope", scope);
            history.add(entry);
        }
        return history;
    }

    private double calculateWeightedScore(Map<String, Double> scores, Map<String, Double> weights) {
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight == 0.0) {
            return 0.0;
        }
        double weightedSum = 0.0;
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            Double weight = weights.getOrDefault(entry.getKey(), 0.0);
            weightedSum += entry.getValue() * weight;
        }
        double score = weightedSum / totalWeight;
        return Math.round(score * 100.0) / 100.0;
    }
}
