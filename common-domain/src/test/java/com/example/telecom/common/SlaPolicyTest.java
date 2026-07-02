package com.example.telecom.common;

import com.example.telecom.common.workorder.SlaPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlaPolicyTest {

    @Test
    void shouldBreachResponseTime() {
        SlaPolicy policy = new SlaPolicy("s1", "CRITICAL", 900000L, 7200000L,
                EscalationLevel.LEVEL_3, true);
        assertTrue(policy.isBreachResponse(1000000L));
        assertFalse(policy.isBreachResponse(500000L));
    }

    @Test
    void shouldBreachResolutionTime() {
        SlaPolicy policy = new SlaPolicy("s2", "HIGH", 3600000L, 14400000L,
                EscalationLevel.LEVEL_2, true);
        assertTrue(policy.isBreachResolution(20000000L));
        assertFalse(policy.isBreachResolution(10000000L));
    }

    @Test
    void shouldNotBreachWhenInactive() {
        SlaPolicy policy = new SlaPolicy("s3", "LOW", 1000L, 1000L,
                EscalationLevel.NONE, false);
        assertFalse(policy.isBreachResponse(5000L));
        assertFalse(policy.isBreachResolution(5000L));
    }
}
