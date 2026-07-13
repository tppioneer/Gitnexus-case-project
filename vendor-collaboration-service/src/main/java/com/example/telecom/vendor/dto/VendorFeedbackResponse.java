package com.example.telecom.vendor.dto;

import java.time.LocalDateTime;

public class VendorFeedbackResponse {
    private String feedbackId;
    private String ticketId;
    private String vendorId;
    private int score;
    private String comment;
    private String submittedBy;
    private LocalDateTime submittedTime;
    private String category;

    public VendorFeedbackResponse() {
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
    public LocalDateTime getSubmittedTime() { return submittedTime; }
    public void setSubmittedTime(LocalDateTime submittedTime) { this.submittedTime = submittedTime; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
