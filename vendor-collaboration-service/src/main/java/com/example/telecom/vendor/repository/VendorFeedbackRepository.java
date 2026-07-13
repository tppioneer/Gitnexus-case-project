package com.example.telecom.vendor.repository;

import com.example.telecom.vendor.domain.VendorFeedback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VendorFeedbackRepository {

    private final ConcurrentHashMap<String, VendorFeedback> store = new ConcurrentHashMap<>();

    public VendorFeedback save(VendorFeedback feedback) {
        store.put(feedback.getFeedbackId(), feedback);
        return feedback;
    }

    public Optional<VendorFeedback> findById(String feedbackId) {
        return Optional.ofNullable(store.get(feedbackId));
    }

    public List<VendorFeedback> findByTicketId(String ticketId) {
        return store.values().stream()
                .filter(f -> f.getTicketId().equals(ticketId))
                .collect(Collectors.toList());
    }

    public List<VendorFeedback> findByVendorId(String vendorId) {
        return store.values().stream()
                .filter(f -> f.getVendorId().equals(vendorId))
                .collect(Collectors.toList());
    }

    public List<VendorFeedback> findByCategory(String category) {
        return store.values().stream()
                .filter(f -> category.equals(f.getCategory()))
                .collect(Collectors.toList());
    }

    public List<VendorFeedback> findByScoreGreaterThanEqual(int minScore) {
        return store.values().stream()
                .filter(f -> f.getScore() >= minScore)
                .collect(Collectors.toList());
    }

    public List<VendorFeedback> findByScoreLessThan(int maxScore) {
        return store.values().stream()
                .filter(f -> f.getScore() < maxScore)
                .collect(Collectors.toList());
    }

    public List<VendorFeedback> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Double> getAverageScore(String vendorId) {
        List<VendorFeedback> feedbacks = findByVendorId(vendorId);
        if (feedbacks.isEmpty()) {
            return Optional.empty();
        }
        double avg = feedbacks.stream()
                .mapToInt(VendorFeedback::getScore)
                .average()
                .orElse(0.0);
        return Optional.of(avg);
    }

    public Optional<Double> getAverageScoreByCategory(String vendorId, String category) {
        List<VendorFeedback> feedbacks = findByVendorId(vendorId).stream()
                .filter(f -> category.equals(f.getCategory()))
                .collect(Collectors.toList());
        if (feedbacks.isEmpty()) {
            return Optional.empty();
        }
        double avg = feedbacks.stream()
                .mapToInt(VendorFeedback::getScore)
                .average()
                .orElse(0.0);
        return Optional.of(avg);
    }

    public long count() {
        return store.size();
    }

    public void delete(String feedbackId) {
        store.remove(feedbackId);
    }

    public long countByVendorId(String vendorId) {
        return store.values().stream()
                .filter(f -> f.getVendorId().equals(vendorId))
                .count();
    }
}
