package com.example.telecom.workorder.repository;

import com.example.telecom.common.workorder.VendorTicket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VendorTicketRepository {
    private final Map<String, VendorTicket> tickets = new ConcurrentHashMap<>();

    public VendorTicket save(VendorTicket ticket) {
        tickets.put(ticket.getVendorTicketId(), ticket);
        return ticket;
    }

    public Optional<VendorTicket> findById(String ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public List<VendorTicket> findByWorkOrderId(String workOrderId) {
        return tickets.values().stream()
                .filter(t -> workOrderId.equals(t.getWorkOrderId()))
                .toList();
    }

    public List<VendorTicket> findByVendor(String vendor) {
        return tickets.values().stream()
                .filter(t -> vendor.equals(t.getVendor()))
                .toList();
    }

    public List<VendorTicket> findAll() {
        return new ArrayList<>(tickets.values());
    }

    public List<VendorTicket> findOpenTickets() {
        return tickets.values().stream()
                .filter(t -> "OPEN".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus()))
                .toList();
    }
}
