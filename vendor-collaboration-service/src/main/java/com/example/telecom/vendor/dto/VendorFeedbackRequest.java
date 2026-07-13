package com.example.telecom.vendor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class VendorFeedbackRequest {

    @NotBlank(message = "Ticket ID is required")
    private String ticketId;

    @NotBlank(message = "Vendor ID is required")
    private String vendorId;

    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 10, message = "Score must not exceed 10")
    private int score;

    @NotBlank(message = "Comment is required")
    private String comment;

    @NotBlank(message = "Submitted by is required")
    private String submittedBy;

    private String category;

    public VendorFeedbackRequest() {
    }

    public VendorFeedbackRequest(String ticketId, String vendorId, int score,
                                 String comment, String submittedBy, String category) {
        this.ticketId = ticketId;
        this.vendorId = vendorId;
        this.score = score;
        this.comment = comment;
        this.submittedBy = submittedBy;
        this.category = category;
    }

    public @NotBlank String getTicketId() { return ticketId; }
    public void setTicketId(@NotBlank String ticketId) { this.ticketId = ticketId; }
    public @NotBlank String getVendorId() { return vendorId; }
    public void setVendorId(@NotBlank String vendorId) { this.vendorId = vendorId; }
    public @Min(1) @Max(10) int getScore() { return score; }
    public void setScore(@Min(1) @Max(10) int score) { this.score = score; }
    public @NotBlank String getComment() { return comment; }
    public void setComment(@NotBlank String comment) { this.comment = comment; }
    public @NotBlank String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(@NotBlank String submittedBy) { this.submittedBy = submittedBy; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
