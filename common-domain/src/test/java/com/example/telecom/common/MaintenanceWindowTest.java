package com.example.telecom.common;

import com.example.telecom.common.maintenance.MaintenanceWindow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceWindowTest {

    @Test
    void shouldCoverTimeWithinWindow() {
        MaintenanceWindow mw = new MaintenanceWindow("w1", "d1", "EAST",
                1000L, 5000L, "Planned maintenance", true);
        assertTrue(mw.coversTime(3000L));
    }

    @Test
    void shouldNotCoverTimeOutsideWindow() {
        MaintenanceWindow mw = new MaintenanceWindow("w1", "d1", "EAST",
                1000L, 5000L, "Planned maintenance", true);
        assertFalse(mw.coversTime(6000L));
        assertFalse(mw.coversTime(500L));
    }

    @Test
    void shouldNotCoverTimeWhenInactive() {
        MaintenanceWindow mw = new MaintenanceWindow("w2", "d2", "WEST",
                1000L, 5000L, "Cancelled", false);
        assertFalse(mw.coversTime(3000L));
    }

    @Test
    void shouldDetectExpiredWindow() {
        MaintenanceWindow mw = new MaintenanceWindow("w3", "d3", "SOUTH",
                1000L, 2000L, "Old", true);
        assertTrue(mw.isExpired());
    }

    @Test
    void regionCodeShouldNotBeCaseCTarget() {
        MaintenanceWindow mw = new MaintenanceWindow("w4", "d4", "MAINT_REGION",
                1000L, 5000L, "test", true);
        assertEquals("MAINT_REGION", mw.getRegionCode());
    }
}
