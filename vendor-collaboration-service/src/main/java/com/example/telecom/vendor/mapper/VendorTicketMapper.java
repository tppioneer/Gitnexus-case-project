package com.example.telecom.vendor.mapper;

import com.example.telecom.vendor.domain.VendorFeedback;
import com.example.telecom.vendor.domain.VendorSlaReport;
import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.dto.*;
import com.example.telecom.vendor.event.VendorTicketEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VendorTicketMapper {

    private VendorTicketMapper() {
    }

    public static VendorTicketResponse toResponse(VendorTicket ticket) {
        if (ticket == null) {
            return null;
        }
        VendorTicketResponse response = new VendorTicketResponse();
        response.setTicketId(ticket.getTicketId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setVendorId(ticket.getVendorId());
        response.setVendorName(ticket.getVendorName());
        response.setDeviceId(ticket.getDeviceId());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setCreatedTime(ticket.getCreatedTime());
        response.setAcknowledgedTime(ticket.getAcknowledgedTime());
        response.setInProgressTime(ticket.getInProgressTime());
        response.setResolvedTime(ticket.getResolvedTime());
        response.setClosedTime(ticket.getClosedTime());
        response.setRegionCode(ticket.getRegionCode());
        response.setAgeInHours(ticket.getAgeInHours());
        response.setEscalationCount(ticket.getEscalationCount());
        return response;
    }

    public static List<VendorTicketResponse> toResponseList(List<VendorTicket> tickets) {
        return tickets.stream()
                .map(VendorTicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static VendorTicketEvent toEvent(VendorTicket ticket, VendorTicketEvent.EventType eventType) {
        return new VendorTicketEvent(
                UUID.randomUUID().toString(),
                ticket.getTicketId(),
                ticket.getVendorId(),
                eventType,
                ticket.getStatus(),
                ticket.getStatus(),
                "Ticket " + eventType.name().toLowerCase() + ": " + ticket.getTitle()
        );
    }

    public static VendorFeedbackResponse toFeedbackResponse(VendorFeedback feedback) {
        if (feedback == null) {
            return null;
        }
        VendorFeedbackResponse response = new VendorFeedbackResponse();
        response.setFeedbackId(feedback.getFeedbackId());
        response.setTicketId(feedback.getTicketId());
        response.setVendorId(feedback.getVendorId());
        response.setScore(feedback.getScore());
        response.setComment(feedback.getComment());
        response.setSubmittedBy(feedback.getSubmittedBy());
        response.setSubmittedTime(feedback.getSubmittedTime());
        response.setCategory(feedback.getCategory());
        return response;
    }

    public static List<VendorFeedbackResponse> toFeedbackResponseList(List<VendorFeedback> feedbacks) {
        return feedbacks.stream()
                .map(VendorTicketMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    public static VendorSlaReportResponse toSlaReportResponse(VendorSlaReport report) {
        if (report == null) {
            return null;
        }
        VendorSlaReportResponse response = new VendorSlaReportResponse();
        response.setReportId(report.getReportId());
        response.setVendorId(report.getVendorId());
        response.setSlaStatus(report.getSlaStatus());
        response.setSlaPercentage(report.getSlaPercentage());
        response.setTicketsMet(report.getTicketsMet());
        response.setTicketsBreached(report.getTicketsBreached());
        response.setTotalTickets(report.getTotalTickets());
        response.setTicketsWarning(report.getTicketsWarning());
        response.setPeriodFrom(report.getPeriodFrom());
        response.setPeriodTo(report.getPeriodTo());
        response.setGeneratedTime(report.getGeneratedTime());
        return response;
    }

    public static List<VendorSlaReportResponse> toSlaReportResponseList(List<VendorSlaReport> reports) {
        return reports.stream()
                .map(VendorTicketMapper::toSlaReportResponse)
                .collect(Collectors.toList());
    }

    public static void updateFromRequest(VendorTicket ticket, VendorTicketRequest request) {
        if (ticket == null || request == null) {
            return;
        }
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setVendorId(request.getVendorId());
        ticket.setVendorName(request.getVendorName());
        ticket.setDeviceId(request.getDeviceId());
        ticket.setPriority(request.getPriority());
        ticket.setRegionCode(request.getRegionCode());
    }

    public static VendorPerformanceReport toPerformanceReport(
            String reportId, String vendorId, double avgResponseTime,
            double percentile95, double resolutionRate, double qualityScore,
            double overallScore, int ticketsProcessed,
            java.time.LocalDate periodFrom, java.time.LocalDate periodTo) {
        return new VendorPerformanceReport(
                reportId, vendorId, avgResponseTime, percentile95,
                resolutionRate, qualityScore, overallScore,
                ticketsProcessed, periodFrom, periodTo
        );
    }

    public static VendorTicket toDomain(VendorTicketRequest request) {
        String ticketId = UUID.randomUUID().toString();
        return new VendorTicket(
                ticketId,
                request.getTitle(),
                request.getDescription(),
                request.getVendorId(),
                request.getVendorName(),
                request.getDeviceId(),
                com.example.telecom.vendor.domain.VendorTicketStatus.CREATED,
                request.getPriority() != null ? request.getPriority() : "MEDIUM",
                request.getRegionCode()
        );
    }

    public static VendorFeedback toFeedbackDomain(VendorFeedbackRequest request) {
        String feedbackId = UUID.randomUUID().toString();
        return new VendorFeedback(
                feedbackId,
                request.getTicketId(),
                request.getVendorId(),
                request.getScore(),
                request.getComment(),
                request.getSubmittedBy(),
                request.getCategory()
        );
    }

    public static VendorSlaReportResponse toSlaReportSummary(VendorSlaReport report, double overallPercentage) {
        VendorSlaReportResponse response = toSlaReportResponse(report);
        if (response != null) {
            response.setSlaPercentage(Math.round(overallPercentage * 100.0) / 100.0);
        }
        return response;
    }
}
