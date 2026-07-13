package com.example.telecom.vendor.repository;

import com.example.telecom.vendor.domain.VendorTicket;
import com.example.telecom.vendor.domain.VendorTicketStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VendorTicketRepository {

    private final ConcurrentHashMap<String, VendorTicket> store = new ConcurrentHashMap<>();

    public VendorTicket save(VendorTicket ticket) {
        store.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    public Optional<VendorTicket> findById(String ticketId) {
        return Optional.ofNullable(store.get(ticketId));
    }

    public List<VendorTicket> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<VendorTicket> findAllSortedByCreatedTimeDesc() {
        return store.values().stream()
                .sorted(Comparator.comparing(VendorTicket::getCreatedTime).reversed())
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findByStatus(VendorTicketStatus status) {
        return store.values().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findByVendorId(String vendorId) {
        return store.values().stream()
                .filter(t -> t.getVendorId().equals(vendorId))
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findByDateRange(LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        return store.values().stream()
                .filter(t -> !t.getCreatedTime().isBefore(fromDateTime)
                        && t.getCreatedTime().isBefore(toDateTime))
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findByVendorIdAndDateRange(String vendorId, LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
        return store.values().stream()
                .filter(t -> t.getVendorId().equals(vendorId)
                        && !t.getCreatedTime().isBefore(fromDateTime)
                        && t.getCreatedTime().isBefore(toDateTime))
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findByStatusAndVendorId(VendorTicketStatus status, String vendorId) {
        return store.values().stream()
                .filter(t -> t.getStatus() == status && t.getVendorId().equals(vendorId))
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findByPriority(String priority) {
        return store.values().stream()
                .filter(t -> priority.equals(t.getPriority()))
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findByRegionCode(String regionCode) {
        return store.values().stream()
                .filter(t -> regionCode.equals(t.getRegionCode()))
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findEscalated() {
        return store.values().stream()
                .filter(t -> t.getStatus() == VendorTicketStatus.ESCALATED)
                .collect(Collectors.toList());
    }

    public List<VendorTicket> findOpenTickets() {
        return store.values().stream()
                .filter(t -> t.getStatus() != VendorTicketStatus.CLOSED
                        && t.getStatus() != VendorTicketStatus.VERIFIED
                        && t.getStatus() != VendorTicketStatus.VENDOR_RESOLVED)
                .collect(Collectors.toList());
    }

    public void delete(String ticketId) {
        store.remove(ticketId);
    }

    public long count() {
        return store.size();
    }

    public long countByVendorId(String vendorId) {
        return store.values().stream()
                .filter(t -> t.getVendorId().equals(vendorId))
                .count();
    }

    public long countByStatus(VendorTicketStatus status) {
        return store.values().stream()
                .filter(t -> t.getStatus() == status)
                .count();
    }
}
