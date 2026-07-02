package com.example.telecom.workorder.service;

import com.example.telecom.common.workorder.VendorTicket;
import com.example.telecom.workorder.dto.VendorTicketRequest;
import com.example.telecom.workorder.mapper.VendorTicketMapper;
import com.example.telecom.workorder.repository.VendorTicketRepository;

import java.util.List;
import java.util.Optional;

public class VendorTicketService {

    private final VendorTicketRepository vendorTicketRepository;
    private final VendorTicketMapper vendorTicketMapper;

    public VendorTicketService(VendorTicketRepository vendorTicketRepository,
                                VendorTicketMapper vendorTicketMapper) {
        this.vendorTicketRepository = vendorTicketRepository;
        this.vendorTicketMapper = vendorTicketMapper;
    }

    public VendorTicket createTicket(VendorTicketRequest request) {
        VendorTicket ticket = vendorTicketMapper.toDomain(request);
        return vendorTicketRepository.save(ticket);
    }

    public Optional<VendorTicket> updateStatus(String ticketId, String newStatus) {
        return vendorTicketRepository.findById(ticketId).map(ticket -> {
            ticket.setStatus(newStatus);
            ticket.setUpdatedTime(System.currentTimeMillis());
            return vendorTicketRepository.save(ticket);
        });
    }

    public List<VendorTicket> findTicketsByWorkOrder(String workOrderId) {
        return vendorTicketRepository.findByWorkOrderId(workOrderId);
    }

    public List<VendorTicket> findOpenTickets() {
        return vendorTicketRepository.findOpenTickets();
    }

    private boolean isValidStatus(String status) {
        return List.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED").contains(status);
    }
}
