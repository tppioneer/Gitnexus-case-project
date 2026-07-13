package com.example.telecom.vendor;

import com.example.telecom.vendor.domain.VendorTicketStatus;
import com.example.telecom.vendor.workflow.VendorCollaborationStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VendorCollaborationStateMachineTest {

    private VendorCollaborationStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new VendorCollaborationStateMachine();
    }

    @Test
    void shouldAllowValidTransitionFromCreatedToAwaitingVendor() {
        assertTrue(stateMachine.isValidTransition(
                VendorTicketStatus.CREATED, VendorTicketStatus.AWAITING_VENDOR));
    }

    @Test
    void shouldRejectInvalidTransitionFromCreatedToClosed() {
        assertFalse(stateMachine.isValidTransition(
                VendorTicketStatus.CREATED, VendorTicketStatus.CLOSED));
    }

    @Test
    void shouldRejectTransitionFromClosedToAnyState() {
        assertFalse(stateMachine.isValidTransition(
                VendorTicketStatus.CLOSED, VendorTicketStatus.CREATED));
        assertFalse(stateMachine.isValidTransition(
                VendorTicketStatus.CLOSED, VendorTicketStatus.VENDOR_IN_PROGRESS));
        assertFalse(stateMachine.isValidTransition(
                VendorTicketStatus.CLOSED, VendorTicketStatus.ESCALATED));
    }

    @Test
    void shouldRejectNullTransitions() {
        assertFalse(stateMachine.isValidTransition(null, VendorTicketStatus.CREATED));
        assertFalse(stateMachine.isValidTransition(VendorTicketStatus.CREATED, null));
        assertFalse(stateMachine.isValidTransition(null, null));
    }

    @Test
    void shouldReturnCorrectNextStatesForVendorAcked() {
        Set<VendorTicketStatus> nextStates = stateMachine.getNextStates(VendorTicketStatus.VENDOR_ACKED);

        assertNotNull(nextStates);
        assertEquals(2, nextStates.size());
        assertTrue(nextStates.contains(VendorTicketStatus.VENDOR_IN_PROGRESS));
        assertTrue(nextStates.contains(VendorTicketStatus.ESCALATED));
    }

    @Test
    void shouldReturnEmptyNextStatesForClosed() {
        Set<VendorTicketStatus> nextStates = stateMachine.getNextStates(VendorTicketStatus.CLOSED);
        assertNotNull(nextStates);
        assertTrue(nextStates.isEmpty());
    }

    @Test
    void shouldReturnEmptyNextStatesForNull() {
        Set<VendorTicketStatus> nextStates = stateMachine.getNextStates(null);
        assertNotNull(nextStates);
        assertTrue(nextStates.isEmpty());
    }

    @Test
    void shouldTransitionThroughFullLifecycle() {
        assertDoesNotThrow(() ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.CREATED, VendorTicketStatus.AWAITING_VENDOR));

        assertDoesNotThrow(() ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.AWAITING_VENDOR, VendorTicketStatus.VENDOR_ACKED));

        assertDoesNotThrow(() ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.VENDOR_ACKED, VendorTicketStatus.VENDOR_IN_PROGRESS));

        assertDoesNotThrow(() ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.VENDOR_IN_PROGRESS, VendorTicketStatus.VENDOR_RESOLVED));

        assertDoesNotThrow(() ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.VENDOR_RESOLVED, VendorTicketStatus.VERIFIED));

        assertDoesNotThrow(() ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.VERIFIED, VendorTicketStatus.CLOSED));
    }

    @Test
    void shouldThrowExceptionForInvalidTransition() {
        assertThrows(IllegalStateException.class, () ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.CREATED, VendorTicketStatus.CLOSED));
    }

    @Test
    void shouldThrowExceptionForSameStateTransition() {
        assertThrows(IllegalStateException.class, () ->
                stateMachine.transition("TICKET-001",
                        VendorTicketStatus.CREATED, VendorTicketStatus.CREATED));
    }

    @Test
    void shouldThrowExceptionForNullStateTransition() {
        assertThrows(IllegalArgumentException.class, () ->
                stateMachine.transition("TICKET-001", null, VendorTicketStatus.CREATED));
    }

    @Test
    void shouldReturnAllStates() {
        Set<VendorTicketStatus> allStates = stateMachine.getAllStates();

        assertNotNull(allStates);
        assertEquals(8, allStates.size());
        assertTrue(allStates.contains(VendorTicketStatus.CREATED));
        assertTrue(allStates.contains(VendorTicketStatus.AWAITING_VENDOR));
        assertTrue(allStates.contains(VendorTicketStatus.VENDOR_ACKED));
        assertTrue(allStates.contains(VendorTicketStatus.VENDOR_IN_PROGRESS));
        assertTrue(allStates.contains(VendorTicketStatus.VENDOR_RESOLVED));
        assertTrue(allStates.contains(VendorTicketStatus.VERIFIED));
        assertTrue(allStates.contains(VendorTicketStatus.CLOSED));
        assertTrue(allStates.contains(VendorTicketStatus.ESCALATED));
    }

    @Test
    void shouldAllowEscalationFromAwaitingVendor() {
        assertTrue(stateMachine.isValidTransition(
                VendorTicketStatus.AWAITING_VENDOR, VendorTicketStatus.ESCALATED));
    }

    @Test
    void shouldAllowReopeningFromEscalatedToInProgress() {
        assertTrue(stateMachine.isValidTransition(
                VendorTicketStatus.ESCALATED, VendorTicketStatus.VENDOR_IN_PROGRESS));
    }

    @Test
    void shouldGetPreviousStates() {
        Set<VendorTicketStatus> previous = stateMachine.getPreviousStates(VendorTicketStatus.VENDOR_ACKED);
        assertNotNull(previous);
        assertTrue(previous.contains(VendorTicketStatus.AWAITING_VENDOR));
    }

    @Test
    void shouldReturnEmptyForPreviousStatesOfCreated() {
        Set<VendorTicketStatus> previous = stateMachine.getPreviousStates(VendorTicketStatus.CREATED);
        assertNotNull(previous);
        assertTrue(previous.isEmpty());
    }

    @Test
    void shouldReturnEmptyForPreviousStatesOfNull() {
        Set<VendorTicketStatus> previous = stateMachine.getPreviousStates(null);
        assertNotNull(previous);
        assertTrue(previous.isEmpty());
    }

    @Test
    void shouldGetStateDescriptions() {
        String description = stateMachine.getStateDescription(VendorTicketStatus.CREATED);
        assertNotNull(description);
        assertFalse(description.isEmpty());
        assertTrue(description.contains("created"));
    }

    @Test
    void shouldReturnUnknownForNullStateDescription() {
        String description = stateMachine.getStateDescription(null);
        assertEquals("Unknown state", description);
    }

    @Test
    void shouldReturnAllStateDescriptions() {
        Map<VendorTicketStatus, String> descriptions = stateMachine.getAllStateDescriptions();
        assertNotNull(descriptions);
        assertEquals(8, descriptions.size());
    }

    @Test
    void shouldIdentifyTerminalAndErrorStates() {
        assertTrue(stateMachine.isTerminalState(VendorTicketStatus.CLOSED));
        assertFalse(stateMachine.isTerminalState(VendorTicketStatus.CREATED));

        assertTrue(stateMachine.isErrorState(VendorTicketStatus.ESCALATED));
        assertFalse(stateMachine.isErrorState(VendorTicketStatus.CLOSED));
    }

    @Test
    void shouldReturnLifecyclePath() {
        List<VendorTicketStatus> path = stateMachine.getLifecyclePath();
        assertNotNull(path);
        assertEquals(7, path.size());
        assertEquals(VendorTicketStatus.CREATED, path.get(0));
        assertEquals(VendorTicketStatus.CLOSED, path.get(path.size() - 1));
    }
}
