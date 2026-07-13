package com.example.telecom.vendor.service;

import com.example.telecom.vendor.domain.VendorFeedback;
import com.example.telecom.vendor.dto.VendorFeedbackRequest;
import com.example.telecom.vendor.dto.VendorFeedbackResponse;
import com.example.telecom.vendor.mapper.VendorTicketMapper;
import com.example.telecom.vendor.repository.VendorFeedbackRepository;
import com.example.telecom.vendor.repository.VendorTicketRepository;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendorFeedbackService {

    private final VendorFeedbackRepository feedbackRepository;
    private final VendorTicketRepository ticketRepository;

    public VendorFeedbackService(VendorFeedbackRepository feedbackRepository,
                                 VendorTicketRepository ticketRepository) {
        this.feedbackRepository = feedbackRepository;
        this.ticketRepository = ticketRepository;
    }

    public VendorFeedbackResponse submitFeedback(VendorFeedbackRequest request) {
        if (request.getTicketId() == null || request.getTicketId().isBlank()) {
            throw new ValidationException("ticketId", "Ticket ID is required");
        }
        if (request.getVendorId() == null || request.getVendorId().isBlank()) {
            throw new ValidationException("vendorId", "Vendor ID is required");
        }
        if (request.getScore() < 1 || request.getScore() > 10) {
            throw new ValidationException("score", "Score must be between 1 and 10");
        }
        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new ValidationException("comment", "Comment is required");
        }
        if (request.getSubmittedBy() == null || request.getSubmittedBy().isBlank()) {
            throw new ValidationException("submittedBy", "Submitter information is required");
        }

        ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new DomainException("TICKET_NOT_FOUND",
                        "Ticket not found: " + request.getTicketId()));

        String feedbackId = UUID.randomUUID().toString();
        VendorFeedback feedback = new VendorFeedback(
                feedbackId,
                request.getTicketId(),
                request.getVendorId(),
                request.getScore(),
                request.getComment(),
                request.getSubmittedBy(),
                request.getCategory()
        );

        VendorFeedback saved = feedbackRepository.save(feedback);
        return VendorTicketMapper.toFeedbackResponse(saved);
    }

    public List<VendorFeedbackResponse> getFeedbackForTicket(String ticketId) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new ValidationException("ticketId", "Ticket ID is required");
        }
        return feedbackRepository.findByTicketId(ticketId).stream()
                .map(VendorTicketMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    public List<VendorFeedbackResponse> getFeedbackForVendor(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new ValidationException("vendorId", "Vendor ID is required");
        }
        return feedbackRepository.findByVendorId(vendorId).stream()
                .map(VendorTicketMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getFeedbackStats(String vendorId) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new ValidationException("vendorId", "Vendor ID is required");
        }

        List<VendorFeedback> feedbacks = feedbackRepository.findByVendorId(vendorId);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("vendorId", vendorId);
        stats.put("totalFeedbacks", feedbacks.size());

        if (feedbacks.isEmpty()) {
            stats.put("averageScore", 0.0);
            stats.put("minScore", 0);
            stats.put("maxScore", 0);
            stats.put("medianScore", 0.0);
            stats.put("scoreDistribution", Collections.emptyMap());
            stats.put("categoryDistribution", Collections.emptyMap());
            return stats;
        }

        double averageScore = calculateAverageScore(feedbacks);
        IntSummaryStatistics summaryStats = feedbacks.stream()
                .mapToInt(VendorFeedback::getScore)
                .summaryStatistics();

        stats.put("averageScore", Math.round(averageScore * 100.0) / 100.0);
        stats.put("minScore", summaryStats.getMin());
        stats.put("maxScore", summaryStats.getMax());

        List<Integer> sortedScores = feedbacks.stream()
                .map(VendorFeedback::getScore)
                .sorted()
                .collect(Collectors.toList());
        int size = sortedScores.size();
        double medianScore;
        if (size % 2 == 0) {
            medianScore = (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0;
        } else {
            medianScore = sortedScores.get(size / 2);
        }
        stats.put("medianScore", medianScore);

        Map<Integer, Long> scoreDistribution = feedbacks.stream()
                .collect(Collectors.groupingBy(VendorFeedback::getScore, Collectors.counting()));
        stats.put("scoreDistribution", scoreDistribution);

        Map<String, Long> categoryDistribution = feedbacks.stream()
                .filter(f -> f.getCategory() != null && !f.getCategory().isBlank())
                .collect(Collectors.groupingBy(VendorFeedback::getCategory, Collectors.counting()));
        stats.put("categoryDistribution", categoryDistribution);

        double standardDeviation = Math.sqrt(feedbacks.stream()
                .mapToDouble(f -> Math.pow(f.getScore() - averageScore, 2))
                .average()
                .orElse(0.0));
        stats.put("standardDeviation", Math.round(standardDeviation * 100.0) / 100.0);

        return stats;
    }

    public Optional<VendorFeedbackResponse> getFeedbackById(String feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .map(VendorTicketMapper::toFeedbackResponse);
    }

    public List<VendorFeedbackResponse> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(VendorTicketMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    private double calculateAverageScore(List<VendorFeedback> feedbacks) {
        return feedbacks.stream()
                .mapToInt(VendorFeedback::getScore)
                .average()
                .orElse(0.0);
    }
}
