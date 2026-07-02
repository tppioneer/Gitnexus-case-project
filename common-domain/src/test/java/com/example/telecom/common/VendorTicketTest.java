package com.example.telecom.common;

import com.example.telecom.common.workorder.VendorTicket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VendorTicketTest {

    @Test
    void shouldConstructVendorTicket() {
        VendorTicket ticket = new VendorTicket("vt-1", "wo-1", "dev-1",
                "Huawei", "OPEN", "Replace optical module");
        assertEquals("vt-1", ticket.getVendorTicketId());
        assertEquals("wo-1", ticket.getWorkOrderId());
        assertEquals("OPEN", ticket.getStatus());
        assertTrue(ticket.getCreatedTime() > 0);
    }

    @Test
    void shouldAllowStatusUpdate() {
        VendorTicket ticket = new VendorTicket("vt-2", "wo-2", "dev-2",
                "ZTE", "OPEN", "Firmware upgrade");
        ticket.setStatus("IN_PROGRESS");
        ticket.setVendorReference("ZTE-REF-12345");
        assertEquals("IN_PROGRESS", ticket.getStatus());
        assertEquals("ZTE-REF-12345", ticket.getVendorReference());
    }

    @Test
    void shouldDefaultToZeroTimestampsForEmptyConstructor() {
        VendorTicket ticket = new VendorTicket();
        assertEquals(0, ticket.getCreatedTime());
    }
}
