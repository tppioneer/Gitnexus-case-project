package com.example.telecom.vendor.evaluation;

import com.example.telecom.vendor.repository.VendorFeedbackRepository;
import com.example.telecom.vendor.domain.VendorFeedback;

import java.util.*;
import java.util.stream.Collectors;

public class QualityScoreVendorEvaluator implements VendorEvaluator {

    private static final double MAX_SCORE = 10.0;
    private static final double EXCELLENT_THRESHOLD = 7.0;

    private final VendorFeedbackRepository feedbackRepository;

    public QualityScoreVendorEvaluator(VendorFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public String getEvaluatorType() {
        return "QUALITY_SCORE";
    }

    @Override
    public EvaluationResult evaluate(String vendorId) {
        List<VendorFeedback> feedbacks = feedbackRepository.findByVendorId(vendorId);

        if (feedbacks.isEmpty()) {
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("totalFeedbacks", 0);
            metrics.put("averageScore", 0.0);
            metrics.put("note", "No feedback data available for evaluation");
            return new EvaluationResult(getEvaluatorType(), 5.0, "No feedback data available", metrics);
        }

        double averageScore = feedbacks.stream()
                .mapToInt(VendorFeedback::getScore)
                .average()
                .orElse(0.0);

        double score = (averageScore / MAX_SCORE) * MAX_SCORE;
        score = Math.min(MAX_SCORE, Math.max(0, score));

        List<Integer> sortedScores = feedbacks.stream()
                .map(VendorFeedback::getScore)
                .sorted()
                .collect(Collectors.toList());
        int size = sortedScores.size();
        double medianScore = size % 2 == 0
                ? (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0
                : sortedScores.get(size / 2);

        IntSummaryStatistics stats = feedbacks.stream()
                .mapToInt(VendorFeedback::getScore)
                .summaryStatistics();

        Map<String, Long> categoryDistribution = feedbacks.stream()
                .filter(f -> f.getCategory() != null && !f.getCategory().isBlank())
                .collect(Collectors.groupingBy(VendorFeedback::getCategory, Collectors.counting()));

        Map<Integer, Long> scoreDistribution = feedbacks.stream()
                .collect(Collectors.groupingBy(VendorFeedback::getScore, Collectors.counting()));

        double standardDeviation = Math.sqrt(feedbacks.stream()
                .mapToDouble(f -> Math.pow(f.getScore() - averageScore, 2))
                .average()
                .orElse(0.0));

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("averageScore", Math.round(averageScore * 100.0) / 100.0);
        metrics.put("medianScore", medianScore);
        metrics.put("minScore", stats.getMin());
        metrics.put("maxScore", stats.getMax());
        metrics.put("totalFeedbacks", feedbacks.size());
        metrics.put("maxPossibleScore", (int) MAX_SCORE);
        metrics.put("standardDeviation", Math.round(standardDeviation * 100.0) / 100.0);
        metrics.put("categoryDistribution", categoryDistribution);
        metrics.put("scoreDistribution", scoreDistribution);

        String description;
        if (averageScore >= EXCELLENT_THRESHOLD) {
            description = "Good quality score: " + String.format("%.2f", averageScore)
                    + " / " + (int) MAX_SCORE + " (above " + String.format("%.0f", EXCELLENT_THRESHOLD) + " threshold)";
        } else {
            description = "Below average quality score: " + String.format("%.2f", averageScore)
                    + " / " + (int) MAX_SCORE + " (below " + String.format("%.0f", EXCELLENT_THRESHOLD) + " threshold)";
        }

        return new EvaluationResult(getEvaluatorType(), Math.round(score * 100.0) / 100.0, description, metrics);
    }

    public Map<String, Object> getQualityMetrics(String vendorId) {
        List<VendorFeedback> feedbacks = feedbackRepository.findByVendorId(vendorId);

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("vendorId", vendorId);
        metrics.put("totalFeedbacks", feedbacks.size());

        if (!feedbacks.isEmpty()) {
            java.util.IntSummaryStatistics stats = feedbacks.stream()
                    .mapToInt(VendorFeedback::getScore)
                    .summaryStatistics();
            metrics.put("averageScore", Math.round(stats.getAverage() * 100.0) / 100.0);
            metrics.put("minScore", stats.getMin());
            metrics.put("maxScore", stats.getMax());
            metrics.put("medianScore", calculateMedian(feedbacks));

            Map<String, Long> categoryDistribution = feedbacks.stream()
                    .filter(f -> f.getCategory() != null)
                    .collect(Collectors.groupingBy(VendorFeedback::getCategory, Collectors.counting()));
            metrics.put("categoryDistribution", categoryDistribution);
        }

        return metrics;
    }

    private double calculateMedian(List<VendorFeedback> feedbacks) {
        List<Integer> sorted = feedbacks.stream()
                .map(VendorFeedback::getScore)
                .sorted()
                .collect(Collectors.toList());
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            return sorted.get(size / 2);
        }
    }
}
