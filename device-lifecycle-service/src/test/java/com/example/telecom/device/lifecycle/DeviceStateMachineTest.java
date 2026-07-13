package com.example.telecom.device.lifecycle;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.model.DeviceLifecycleState;
import com.example.telecom.device.lifecycle.workflow.DeviceStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DeviceStateMachineTest {

    private DeviceStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new DeviceStateMachine();
    }

    @Test
    void testValidTransition() {
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.REGISTERED, DeviceLifecycleState.ACTIVE));
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.ACTIVE, DeviceLifecycleState.SUSPENDED));
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.SUSPENDED, DeviceLifecycleState.ACTIVE));
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.ACTIVE, DeviceLifecycleState.RETIRED));
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.RETIRED, DeviceLifecycleState.DECOMMISSIONED));
    }

    @Test
    void testInvalidTransitionThrowsException() {
        assertFalse(stateMachine.isValidTransition(DeviceLifecycleState.REGISTERED, DeviceLifecycleState.DECOMMISSIONED));
        assertFalse(stateMachine.isValidTransition(DeviceLifecycleState.REGISTERED, DeviceLifecycleState.SUSPENDED));
        assertFalse(stateMachine.isValidTransition(DeviceLifecycleState.ACTIVE, DeviceLifecycleState.REGISTERED));
        assertFalse(stateMachine.isValidTransition(DeviceLifecycleState.DECOMMISSIONED, DeviceLifecycleState.ACTIVE));
        assertFalse(stateMachine.isValidTransition(DeviceLifecycleState.SUSPENDED, DeviceLifecycleState.RETIRED));

        assertThrows(DomainException.class, () ->
                stateMachine.transition(DeviceLifecycleState.REGISTERED, DeviceLifecycleState.DECOMMISSIONED, "DEV-001"));
    }

    @Test
    void testGetNextStates() {
        Set<DeviceLifecycleState> registeredNext = stateMachine.getNextStates(DeviceLifecycleState.REGISTERED);
        assertEquals(1, registeredNext.size());
        assertTrue(registeredNext.contains(DeviceLifecycleState.ACTIVE));

        Set<DeviceLifecycleState> activeNext = stateMachine.getNextStates(DeviceLifecycleState.ACTIVE);
        assertEquals(3, activeNext.size());
        assertTrue(activeNext.contains(DeviceLifecycleState.SUSPENDED));
        assertTrue(activeNext.contains(DeviceLifecycleState.RETIRED));
        assertTrue(activeNext.contains(DeviceLifecycleState.FIRMWARE_UPGRADING));

        Set<DeviceLifecycleState> decommissionedNext = stateMachine.getNextStates(DeviceLifecycleState.DECOMMISSIONED);
        assertTrue(decommissionedNext.isEmpty());
    }

    @Test
    void testFirmwareUpgradeTransition() {
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.ACTIVE, DeviceLifecycleState.FIRMWARE_UPGRADING));
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.FIRMWARE_UPGRADING, DeviceLifecycleState.ACTIVE));
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.FIRMWARE_UPGRADING, DeviceLifecycleState.FAILED));

        DeviceLifecycleState result = stateMachine.transition(
                DeviceLifecycleState.ACTIVE, DeviceLifecycleState.FIRMWARE_UPGRADING, "DEV-002");
        assertEquals(DeviceLifecycleState.FIRMWARE_UPGRADING, result);
    }

    @Test
    void testFailedToActiveTransition() {
        assertTrue(stateMachine.isValidTransition(DeviceLifecycleState.FAILED, DeviceLifecycleState.ACTIVE));
        assertFalse(stateMachine.isValidTransition(DeviceLifecycleState.FAILED, DeviceLifecycleState.REGISTERED));
        assertFalse(stateMachine.isValidTransition(DeviceLifecycleState.FAILED, DeviceLifecycleState.SUSPENDED));

        DeviceLifecycleState result = stateMachine.transition(
                DeviceLifecycleState.FAILED, DeviceLifecycleState.ACTIVE, "DEV-003");
        assertEquals(DeviceLifecycleState.ACTIVE, result);
    }

    @Test
    void testGetAllStates() {
        List<DeviceLifecycleState> allStates = stateMachine.getAllStates();
        assertNotNull(allStates);
        assertTrue(allStates.contains(DeviceLifecycleState.REGISTERED));
        assertTrue(allStates.contains(DeviceLifecycleState.ACTIVE));
        assertTrue(allStates.contains(DeviceLifecycleState.SUSPENDED));
        assertTrue(allStates.contains(DeviceLifecycleState.RETIRED));
        assertTrue(allStates.contains(DeviceLifecycleState.DECOMMISSIONED));
        assertTrue(allStates.contains(DeviceLifecycleState.FIRMWARE_UPGRADING));
        assertTrue(allStates.contains(DeviceLifecycleState.FAILED));
        assertEquals(7, allStates.size());
    }

    @Test
    void testHasNextStates() {
        assertTrue(stateMachine.hasNextStates(DeviceLifecycleState.REGISTERED));
        assertTrue(stateMachine.hasNextStates(DeviceLifecycleState.ACTIVE));
        assertFalse(stateMachine.hasNextStates(DeviceLifecycleState.DECOMMISSIONED));
    }

    @Test
    void testGetTransitionCount() {
        assertEquals(1, stateMachine.getTransitionCount(DeviceLifecycleState.REGISTERED));
        assertEquals(3, stateMachine.getTransitionCount(DeviceLifecycleState.ACTIVE));
        assertEquals(0, stateMachine.getTransitionCount(DeviceLifecycleState.DECOMMISSIONED));
    }

    @Test
    void testCanTransitionFrom() {
        assertTrue(stateMachine.canTransitionFrom(DeviceLifecycleState.REGISTERED));
        assertFalse(stateMachine.canTransitionFrom(DeviceLifecycleState.DECOMMISSIONED));
    }

    @Test
    void testCanTransitionTo() {
        assertTrue(stateMachine.canTransitionTo(DeviceLifecycleState.ACTIVE));
        assertTrue(stateMachine.canTransitionTo(DeviceLifecycleState.DECOMMISSIONED));
        assertFalse(stateMachine.canTransitionTo(DeviceLifecycleState.REGISTERED));
    }

    @Test
    void testGetSourceStates() {
        List<DeviceLifecycleState> activeSources = stateMachine.getSourceStates(DeviceLifecycleState.ACTIVE);
        assertTrue(activeSources.contains(DeviceLifecycleState.REGISTERED));
        assertTrue(activeSources.contains(DeviceLifecycleState.SUSPENDED));
        assertTrue(activeSources.contains(DeviceLifecycleState.FIRMWARE_UPGRADING));
        assertTrue(activeSources.contains(DeviceLifecycleState.FAILED));
        assertEquals(4, activeSources.size());
    }

    @Test
    void testTransitionConvenienceOverload() {
        DeviceLifecycleState result = stateMachine.transition("DEV-100", DeviceLifecycleState.ACTIVE);
        assertEquals(DeviceLifecycleState.ACTIVE, result);
    }
}
