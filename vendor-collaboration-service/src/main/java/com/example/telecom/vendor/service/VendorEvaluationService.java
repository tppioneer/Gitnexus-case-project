package com.example.telecom.vendor.service;

import com.example.telecom.vendor.evaluation.EvaluationResult;
import com.example.telecom.vendor.evaluation.VendorEvaluator;
import com.example.telecom.vendor.evaluation.VendorEvaluatorRegistry;
import com.example.telecom.vendor.repository.VendorPerformanceRepository;
import com.example.telecom.vendor.dto.VendorPerformanceReport;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendorEvaluationService {

    private final VendorEvaluatorRegistry evaluatorRegistry;
    private final VendorPerformanceRepository performanceRepository;

    public VendorEvaluationService(VendorEvaluatorRegistry evaluatorRegistry,
                                   VendorPerformanceRepository performanceRepository) {
        this.evaluatorRegistry = evaluatorRegistry;
        this.performanceRepository = performanceRepository;
    }

    public Map<String, Object> evaluateVendor(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new IllegalArgumentException("Vendor ID must not be blank");
        }

        List<EvaluationResult> results = evaluatorRegistry.evaluateAll(vendorId);

        Map<String, Object> evaluation = new LinkedHashMap<>();
        evaluation.put("vendorId", vendorId);
        evaluation.put("evaluations", results.stream()
                .collect(Collectors.toMap(
                        EvaluationResult::getEvaluatorType,
                        r -> {
                            Map<String, Object> detail = new LinkedHashMap<>();
                            detail.put("score", r.getScore());
                            detail.put("description", r.getDescription());
                            detail.put("metrics", r.getMetrics());
                            return detail;
                        }
                )));

        double overallScore = aggregateScores(results);
        evaluation.put("overallScore", Math.round(overallScore * 100.0) / 100.0);

        String rating = overallScore >= 8.0 ? "EXCELLENT"
                : overallScore >= 6.0 ? "GOOD"
                : overallScore >= 4.0 ? "AVERAGE"
                : "POOR";
        evaluation.put("rating", rating);

        Map<String, Object> strengths = new LinkedHashMap<>();
        Map<String, Object> weaknesses = new LinkedHashMap<>();
        for (EvaluationResult result : results) {
            if (result.getScore() >= 7.0) {
                strengths.put(result.getEvaluatorType(), result.getDescription());
            } else if (result.getScore() < 5.0) {
                weaknesses.put(result.getEvaluatorType(), result.getDescription());
            }
        }
        evaluation.put("strengths", strengths);
        evaluation.put("weaknesses", weaknesses);

        return evaluation;
    }

    public Optional<Double> getEvaluationScore(String vendorId) {
        List<EvaluationResult> results = evaluatorRegistry.evaluateAll(vendorId);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Math.round(aggregateScores(results) * 100.0) / 100.0);
    }

    public List<Map<String, Object>> getVendorRanking() {
        List<VendorPerformanceReport> allReports = performanceRepository.findAll();

        if (allReports.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<VendorPerformanceReport>> groupedByVendor = allReports.stream()
                .collect(Collectors.groupingBy(VendorPerformanceReport::getVendorId));

        List<Map<String, Object>> rankings = new ArrayList<>();

        for (Map.Entry<String, List<VendorPerformanceReport>> entry : groupedByVendor.entrySet()) {
            String vendorId = entry.getKey();
            List<VendorPerformanceReport> reports = entry.getValue();

            double avgOverallScore = reports.stream()
                    .mapToDouble(VendorPerformanceReport::getOverallScore)
                    .average()
                    .orElse(0.0);

            double avgResponseTime = reports.stream()
                    .mapToDouble(VendorPerformanceReport::getAverageResponseTimeHours)
                    .average()
                    .orElse(0.0);

            double avgResolutionRate = reports.stream()
                    .mapToDouble(VendorPerformanceReport::getResolutionRate)
                    .average()
                    .orElse(0.0);

            int totalTicketsProcessed = reports.stream()
                    .mapToInt(VendorPerformanceReport::getTicketsProcessed)
                    .sum();

            Map<String, Object> vendorRanking = new LinkedHashMap<>();
            vendorRanking.put("vendorId", vendorId);
            vendorRanking.put("overallScore", Math.round(avgOverallScore * 100.0) / 100.0);
            vendorRanking.put("averageResponseTimeHours", Math.round(avgResponseTime * 100.0) / 100.0);
            vendorRanking.put("averageResolutionRate", Math.round(avgResolutionRate * 10000.0) / 10000.0);
            vendorRanking.put("totalTicketsProcessed", totalTicketsProcessed);
            vendorRanking.put("reportCount", reports.size());

            rankings.add(vendorRanking);
        }

        rankings.sort((a, b) -> Double.compare(
                (Double) b.get("overallScore"),
                (Double) a.get("overallScore")));

        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).put("rank", i + 1);
        }

        return rankings;
    }

    public Map<String, Object> getDetailedEvaluation(String vendorId) {
        Map<String, Object> evaluation = evaluateVendor(vendorId);

        List<EvaluationResult> results = evaluatorRegistry.evaluateAll(vendorId);
        Map<String, Object> detailedMetrics = new LinkedHashMap<>();

        for (EvaluationResult result : results) {
            Map<String, Object> evaluatorDetail = new LinkedHashMap<>();
            evaluatorDetail.put("score", result.getScore());
            evaluatorDetail.put("description", result.getDescription());
            evaluatorDetail.put("metrics", result.getMetrics());

            double weight;
            switch (result.getEvaluatorType()) {
                case "RESOLUTION_RATE":
                    weight = 0.4;
                    break;
                case "RESPONSE_TIME":
                    weight = 0.35;
                    break;
                case "QUALITY_SCORE":
                    weight = 0.25;
                    break;
                default:
                    weight = 1.0 / results.size();
            }
            evaluatorDetail.put("weight", weight);
            detailedMetrics.put(result.getEvaluatorType(), evaluatorDetail);
        }

        evaluation.put("detailedMetrics", detailedMetrics);
        evaluation.put("evaluationTimestamp", System.currentTimeMillis());

        return evaluation;
    }

    private double aggregateScores(List<EvaluationResult> results) {
        if (results.isEmpty()) {
            return 0.0;
        }
        return results.stream()
                .mapToDouble(EvaluationResult::getScore)
                .average()
                .orElse(0.0);
    }
}
