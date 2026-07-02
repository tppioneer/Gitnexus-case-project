package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.workorder.dto.VendorTicketRequest;
import com.example.telecom.workorder.dto.VendorTicketResponse;
import com.example.telecom.workorder.mapper.VendorTicketMapper;
import com.example.telecom.workorder.service.VendorTicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-tickets")
public class VendorTicketController {

    private final VendorTicketService vendorTicketService;
    private final VendorTicketMapper vendorTicketMapper;

    public VendorTicketController(VendorTicketService vendorTicketService,
                                   VendorTicketMapper vendorTicketMapper) {
        this.vendorTicketService = vendorTicketService;
        this.vendorTicketMapper = vendorTicketMapper;
    }

    @PostMapping
    public ApiResponse<VendorTicketResponse> createTicket(@RequestBody VendorTicketRequest request) {
        return ApiResponse.success(
                vendorTicketMapper.toResponse(vendorTicketService.createTicket(request)));
    }

    @GetMapping
    public ApiResponse<List<VendorTicketResponse>> listTickets(
            @RequestParam(required = false) String workOrderId) {
        var tickets = workOrderId != null
                ? vendorTicketService.findTicketsByWorkOrder(workOrderId)
                : vendorTicketService.findOpenTickets();
        return ApiResponse.success(tickets.stream().map(vendorTicketMapper::toResponse).toList());
    }

    @PutMapping("/{ticketId}/status")
    public ApiResponse<VendorTicketResponse> updateStatus(
            @PathVariable String ticketId, @RequestBody String status) {
        return vendorTicketService.updateStatus(ticketId, status)
                .map(vendorTicketMapper::toResponse)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Vendor ticket not found"));
    }
}
