package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.DispatchResult;
import com.example.telecom.change.domain.ChangeCommand;
import com.example.telecom.change.domain.EmergencyChangeCommand;
import com.example.telecom.change.domain.ChangeRisk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test: verifies overload resolution at the Java language level.
 * This is a pure Java unit test — no Spring context needed.
 */
class ChangeCommandBusOverloadTest {

    @Test
    void dispatch_baseCommand_callsBaseOverload() {
        ChangeCommandBus bus = new ChangeCommandBus();
        ChangeCommand cmd = new ChangeCommand("CHG-OV-001", ChangeMode.LIVE, "base");
        DispatchResult result = bus.dispatch((ChangeCommand) cmd);
        assertEquals("ChangeCommand", result.getDispatchedTo());
    }

    @Test
    void dispatch_emergencyCommand_callsEmergencyOverload() {
        // Even without explicit cast, the compiler binds EmergencyChangeCommand
        // to the dispatch(EmergencyChangeCommand) overload
        ChangeCommandBus bus = new ChangeCommandBus();
        EmergencyChangeCommand cmd = new EmergencyChangeCommand(
                "CHG-OV-002", ChangeMode.LIVE, "emergency", ChangeRisk.CRITICAL, "ops");
        DispatchResult result = bus.dispatch(cmd);
        assertEquals("EmergencyChangeCommand", result.getDispatchedTo());
    }

    @Test
    void dispatch_stringAndMode_callsThirdOverload() {
        ChangeCommandBus bus = new ChangeCommandBus();
        DispatchResult result = bus.dispatch("CHG-OV-003", ChangeMode.DRY_RUN);
        assertEquals("ChangeId+Mode", result.getDispatchedTo());
    }

    @Test
    void emergencyCommand_upcastToBase_callsBaseOverload() {
        // When upcast to ChangeCommand, the base overload is selected
        ChangeCommandBus bus = new ChangeCommandBus();
        ChangeCommand cmd = new EmergencyChangeCommand(
                "CHG-OV-004", ChangeMode.LIVE, "emergency", ChangeRisk.HIGH, "ops");
        DispatchResult result = bus.dispatch((ChangeCommand) cmd);
        assertEquals("ChangeCommand", result.getDispatchedTo(),
                "Upcast to ChangeCommand must resolve to base overload");
    }

    @Test
    void each_overload_producesUniqueDetails() {
        ChangeCommandBus bus = new ChangeCommandBus();

        DispatchResult r1 = bus.dispatch(new ChangeCommand("CHG-A", ChangeMode.LIVE, "base"));
        DispatchResult r2 = bus.dispatch(new EmergencyChangeCommand(
                "CHG-B", ChangeMode.LIVE, "emerg", ChangeRisk.HIGH, "ops"));
        DispatchResult r3 = bus.dispatch("CHG-C", ChangeMode.DRY_RUN);

        // Each overload produces different detail strings
        assertNotEquals(r1.getDetails(), r2.getDetails());
        assertNotEquals(r2.getDetails(), r3.getDetails());
    }

    @Test
    void dispatch_primitiveInt_callsPrimitiveOverload() {
        ChangeCommandBus bus = new ChangeCommandBus();
        int count = 3;
        DispatchResult result = bus.dispatch(count);
        assertEquals("retryCount:primitive", result.getDispatchedTo());
    }

    @Test
    void dispatch_boxedInteger_callsBoxedOverload() {
        ChangeCommandBus bus = new ChangeCommandBus();
        Integer count = Integer.valueOf(3);
        DispatchResult result = bus.dispatch(count);
        assertEquals("retryCount:boxed", result.getDispatchedTo());
    }

    @Test
    void dispatchAll_callsAllThreeOverloads() {
        ChangeCommandBus bus = new ChangeCommandBus();
        ChangeCommand base = new ChangeCommand("CHG-ALL-001", ChangeMode.LIVE, "base");
        EmergencyChangeCommand emergency = new EmergencyChangeCommand(
                "CHG-ALL-002", ChangeMode.LIVE, "emergency", ChangeRisk.HIGH, "ops");
        DispatchResult result = bus.dispatchAll(base, emergency, "CHG-ALL-003", ChangeMode.DRY_RUN);
        // dispatchAll combines the three per-call dispatchedTo into details
        assertEquals("ChangeCommand,EmergencyChangeCommand,ChangeId+Mode", result.getDetails());
    }
}
