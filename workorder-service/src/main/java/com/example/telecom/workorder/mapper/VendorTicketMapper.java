package com.example.telecom.workorder.mapper;

import com.example.telecom.common.workorder.VendorTicket;
import com.example.telecom.workorder.dto.VendorTicketRequest;
import com.example.telecom.workorder.dto.VendorTicketResponse;

import java.util.UUID;

public class VendorTicketMapper {

    public VendorTicket toDomain(VendorTicketRequest request) {
        return new VendorTicket(
                UUID.randomUUID().toString(),
                request.getWorkOrderId(),
                request.getDeviceId(),
                request.getVendor(),
                "OPEN",
                request.getDescription()
        );
    }

    public VendorTicketResponse toResponse(VendorTicket ticket) {
        VendorTicketResponse response = new VendorTicketResponse();
        response.setVendorTicketId(ticket.getVendorTicketId());
        response.setWorkOrderId(ticket.getWorkOrderId());
        response.setVendor(ticket.getVendor());
        response.setStatus(ticket.getStatus());
        response.setVendorReference(ticket.getVendorReference());
        response.setCreatedTime(ticket.getCreatedTime());
        return response;
    }
}
