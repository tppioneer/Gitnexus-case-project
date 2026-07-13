package com.example.telecom.vendor.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class VendorFeedback {
    private String feedbackId;
    private String ticketId;
    private String vendorId;
    private int score;
    private String comment;
    private String submittedBy;
    private LocalDateTime submittedTime;
    private String category;

    public VendorFeedback() {
    }

    public VendorFeedback(String feedbackId, String ticketId, String vendorId, int score,
                          String comment, String submittedBy, String category) {
        this.feedbackId = feedbackId;
        this.ticketId = ticketId;
        this.vendorId = vendorId;
        this.score = score;
        this.comment = comment;
        this.submittedBy = submittedBy;
        this.category = category;
        this.submittedTime = LocalDateTime.now();
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

    public boolean isPositiveFeedback() {
        return score >= 7;
    }

    public boolean isNegativeFeedback() {
        return score <= 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorFeedback that = (VendorFeedback) o;
        return Objects.equals(feedbackId, that.feedbackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackId);
    }

    @Override
    public String toString() {
        return "VendorFeedback{" +
                "feedbackId='" + feedbackId + '\'' +
                ", ticketId='" + ticketId + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", score=" + score +
                ", category='" + category + '\'' +
                '}';
    }
}
