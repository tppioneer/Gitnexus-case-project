package com.example.telecom.vendor.controller;

import com.example.telecom.vendor.dto.VendorFeedbackRequest;
import com.example.telecom.vendor.dto.VendorFeedbackResponse;
import com.example.telecom.vendor.service.VendorFeedbackService;
import com.example.telecom.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor/feedback")
public class VendorFeedbackController {

    private final VendorFeedbackService feedbackService;

    public VendorFeedbackController(VendorFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VendorFeedbackResponse>> submitFeedback(
            @Valid @RequestBody VendorFeedbackRequest request) {
        VendorFeedbackResponse response = feedbackService.submitFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<List<VendorFeedbackResponse>>> getFeedbackForTicket(
            @PathVariable String ticketId) {
        List<VendorFeedbackResponse> feedbacks = feedbackService.getFeedbackForTicket(ticketId);
        return ResponseEntity.ok(ApiResponse.success(feedbacks));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<List<VendorFeedbackResponse>>> getFeedbackForVendor(
            @PathVariable String vendorId) {
        List<VendorFeedbackResponse> feedbacks = feedbackService.getFeedbackForVendor(vendorId);
        return ResponseEntity.ok(ApiResponse.success(feedbacks));
    }

    @GetMapping("/stats/{vendorId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFeedbackStats(
            @PathVariable String vendorId) {
        Map<String, Object> stats = feedbackService.getFeedbackStats(vendorId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/detail/{feedbackId}")
    public ResponseEntity<ApiResponse<VendorFeedbackResponse>> getFeedbackById(
            @PathVariable String feedbackId) {
        return feedbackService.getFeedbackById(feedbackId)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Feedback not found: " + feedbackId)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VendorFeedbackResponse>>> getAllFeedback() {
        List<VendorFeedbackResponse> feedbacks = feedbackService.getAllFeedback();
        return ResponseEntity.ok(ApiResponse.success(feedbacks));
    }
}
