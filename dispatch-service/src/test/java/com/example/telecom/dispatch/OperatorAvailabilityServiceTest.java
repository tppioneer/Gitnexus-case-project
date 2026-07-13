package com.example.telecom.dispatch;

import com.example.telecom.dispatch.domain.Operator;
import com.example.telecom.dispatch.service.OperatorAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperatorAvailabilityServiceTest {

    private OperatorAvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        availabilityService = new OperatorAvailabilityService();
        availabilityService.init();
    }

    @Test
    void testCheckAvailability() {
        availabilityService.setAvailable("OP-001");
        assertTrue(availabilityService.check("OP-001"));

        availabilityService.setBusy("OP-001");
        assertFalse(availabilityService.check("OP-001"));
    }

    @Test
    void testSetAvailableAndBusy() {
        availabilityService.setAvailable("OP-001");
        assertTrue(availabilityService.check("OP-001"));

        availabilityService.setBusy("OP-001");
        assertFalse(availabilityService.check("OP-001"));

        availabilityService.setAvailable("OP-001");
        assertTrue(availabilityService.check("OP-001"));
    }

    @Test
    void testGetAvailableOperators() {
        availabilityService.setAvailable("OP-001");
        availabilityService.setBusy("OP-002");
        availabilityService.setAvailable("OP-003");

        List<Operator> available = availabilityService.getAvailableOperators();

        List<String> ids = available.stream().map(Operator::getId).toList();
        assertTrue(ids.contains("OP-001"));
        assertFalse(ids.contains("OP-002"));
        assertTrue(ids.contains("OP-003"));
    }

    @Test
    void testOperatorLoad() {
        assertEquals(0, availabilityService.getOperatorLoad("OP-001"));

        availabilityService.incrementLoad("OP-001");
        assertEquals(1, availabilityService.getOperatorLoad("OP-001"));

        availabilityService.incrementLoad("OP-001");
        assertEquals(2, availabilityService.getOperatorLoad("OP-001"));

        availabilityService.decrementLoad("OP-001");
        assertEquals(1, availabilityService.getOperatorLoad("OP-001"));
    }

    @Test
    void testSetOffline() {
        availabilityService.setAvailable("OP-001");
        assertTrue(availabilityService.check("OP-001"));

        availabilityService.setOffline("OP-001");
        assertFalse(availabilityService.check("OP-001"));
    }
}
