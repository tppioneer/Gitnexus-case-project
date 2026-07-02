package com.example.telecom.workorder;

import com.example.telecom.common.workorder.VendorTicket;
import com.example.telecom.workorder.dto.VendorTicketRequest;
import com.example.telecom.workorder.mapper.VendorTicketMapper;
import com.example.telecom.workorder.repository.VendorTicketRepository;
import com.example.telecom.workorder.service.VendorTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VendorTicketServiceTest {

    private VendorTicketService vendorTicketService;
    private VendorTicketRepository vendorTicketRepository;

    @BeforeEach
    void setUp() {
        vendorTicketRepository = new VendorTicketRepository();
        VendorTicketMapper mapper = new VendorTicketMapper();
        vendorTicketService = new VendorTicketService(vendorTicketRepository, mapper);
    }

    @Test
    void shouldCreateVendorTicket() {
        VendorTicketRequest request = new VendorTicketRequest();
        request.setWorkOrderId("wo-1");
        request.setDeviceId("dev-1");
        request.setVendor("Huawei");
        request.setDescription("Replace faulty optical module");

        VendorTicket ticket = vendorTicketService.createTicket(request);
        assertNotNull(ticket.getVendorTicketId());
        assertEquals("wo-1", ticket.getWorkOrderId());
        assertEquals("OPEN", ticket.getStatus());
    }

    @Test
    void shouldUpdateTicketStatus() {
        VendorTicketRequest request = createRequest("wo-2", "dev-2", "ZTE");
        VendorTicket ticket = vendorTicketService.createTicket(request);

        var updated = vendorTicketService.updateStatus(ticket.getVendorTicketId(), "IN_PROGRESS");
        assertTrue(updated.isPresent());
        assertEquals("IN_PROGRESS", updated.get().getStatus());
    }

    @Test
    void shouldFindTicketsByWorkOrder() {
        vendorTicketService.createTicket(createRequest("wo-3", "dev-3", "Huawei"));
        vendorTicketService.createTicket(createRequest("wo-3", "dev-4", "ZTE"));
        vendorTicketService.createTicket(createRequest("wo-other", "dev-5", "FiberHome"));

        List<VendorTicket> tickets = vendorTicketService.findTicketsByWorkOrder("wo-3");
        assertEquals(2, tickets.size());
    }

    @Test
    void shouldFindOpenTickets() {
        vendorTicketService.createTicket(createRequest("wo-4", "dev-6", "Cisco"));
        assertEquals(1, vendorTicketService.findOpenTickets().size());
    }

    private VendorTicketRequest createRequest(String woId, String deviceId, String vendor) {
        VendorTicketRequest request = new VendorTicketRequest();
        request.setWorkOrderId(woId);
        request.setDeviceId(deviceId);
        request.setVendor(vendor);
        request.setDescription("Test vendor ticket");
        return request;
    }
}
