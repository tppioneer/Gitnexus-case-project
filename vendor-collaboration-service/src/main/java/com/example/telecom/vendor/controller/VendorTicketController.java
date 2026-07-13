package com.example.telecom.vendor.controller;

import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.dto.VendorTicketRequest;
import com.example.telecom.vendor.dto.VendorTicketResponse;
import com.example.telecom.vendor.service.VendorTicketService;
import com.example.telecom.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendor/tickets")
public class VendorTicketController {

    private final VendorTicketService vendorTicketService;

    public VendorTicketController(VendorTicketService vendorTicketService) {
        this.vendorTicketService = vendorTicketService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VendorTicketResponse>> createTicket(
            @Valid @RequestBody VendorTicketRequest request) {
        VendorTicketResponse response = vendorTicketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<VendorTicketResponse>> getTicket(
            @PathVariable String ticketId) {
        return vendorTicketService.getTicket(ticketId)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Ticket not found: " + ticketId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorTicketResponse>>> listTickets(
            @RequestParam(required = false) VendorTicketStatus status,
            @RequestParam(required = false) String vendorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<VendorTicketResponse> tickets = vendorTicketService.listTickets(status, vendorId, dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @PutMapping("/{ticketId}/ack")
    public ResponseEntity<ApiResponse<VendorTicketResponse>> acknowledgeTicket(
            @PathVariable String ticketId) {
        VendorTicketResponse response = vendorTicketService.acknowledgeTicket(ticketId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{ticketId}/progress")
    public ResponseEntity<ApiResponse<VendorTicketResponse>> markInProgress(
            @PathVariable String ticketId) {
        VendorTicketResponse response = vendorTicketService.markInProgress(ticketId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<ApiResponse<VendorTicketResponse>> resolveTicket(
            @PathVariable String ticketId) {
        VendorTicketResponse response = vendorTicketService.resolveTicket(ticketId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{ticketId}/close")
    public ResponseEntity<ApiResponse<VendorTicketResponse>> closeTicket(
            @PathVariable String ticketId) {
        VendorTicketResponse response = vendorTicketService.closeTicket(ticketId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{ticketId}/escalate")
    public ResponseEntity<ApiResponse<VendorTicketResponse>> escalateTicket(
            @PathVariable String ticketId) {
        VendorTicketResponse response = vendorTicketService.escalateTicket(ticketId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<List<VendorTicketResponse>>> getTicketsByVendor(
            @PathVariable String vendorId) {
        List<VendorTicketResponse> tickets = vendorTicketService.listTickets(null, vendorId, null, null);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<VendorTicketResponse>>> getTicketsByStatus(
            @PathVariable VendorTicketStatus status) {
        List<VendorTicketResponse> tickets = vendorTicketService.listTickets(status, null, null, null);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }
}
