package com.example.telecom.vendor.workflow;

import com.example.telecom.vendor.domain.VendorTicketStatus;

import java.util.*;

public class VendorCollaborationStateMachine {

    private static final Map<VendorTicketStatus, Set<VendorTicketStatus>> VALID_TRANSITIONS = new EnumMap<>(VendorTicketStatus.class);

    private static final Map<VendorTicketStatus, String> STATE_DESCRIPTIONS = new EnumMap<>(VendorTicketStatus.class);

    static {
        VALID_TRANSITIONS.put(VendorTicketStatus.CREATED, Set.of(VendorTicketStatus.AWAITING_VENDOR));
        VALID_TRANSITIONS.put(VendorTicketStatus.AWAITING_VENDOR, Set.of(VendorTicketStatus.VENDOR_ACKED, VendorTicketStatus.ESCALATED));
        VALID_TRANSITIONS.put(VendorTicketStatus.VENDOR_ACKED, Set.of(VendorTicketStatus.VENDOR_IN_PROGRESS, VendorTicketStatus.ESCALATED));
        VALID_TRANSITIONS.put(VendorTicketStatus.VENDOR_IN_PROGRESS, Set.of(VendorTicketStatus.VENDOR_RESOLVED, VendorTicketStatus.ESCALATED));
        VALID_TRANSITIONS.put(VendorTicketStatus.VENDOR_RESOLVED, Set.of(VendorTicketStatus.VERIFIED, VendorTicketStatus.VENDOR_IN_PROGRESS));
        VALID_TRANSITIONS.put(VendorTicketStatus.VERIFIED, Set.of(VendorTicketStatus.CLOSED));
        VALID_TRANSITIONS.put(VendorTicketStatus.CLOSED, Collections.emptySet());
        VALID_TRANSITIONS.put(VendorTicketStatus.ESCALATED, Set.of(VendorTicketStatus.VENDOR_IN_PROGRESS));

        STATE_DESCRIPTIONS.put(VendorTicketStatus.CREATED, "Ticket has been created and is pending assignment to vendor");
        STATE_DESCRIPTIONS.put(VendorTicketStatus.AWAITING_VENDOR, "Ticket has been assigned to vendor and is awaiting acknowledgment");
        STATE_DESCRIPTIONS.put(VendorTicketStatus.VENDOR_ACKED, "Vendor has acknowledged the ticket and will begin work");
        STATE_DESCRIPTIONS.put(VendorTicketStatus.VENDOR_IN_PROGRESS, "Vendor is actively working on resolving the ticket");
        STATE_DESCRIPTIONS.put(VendorTicketStatus.VENDOR_RESOLVED, "Vendor has resolved the issue and is awaiting verification");
        STATE_DESCRIPTIONS.put(VendorTicketStatus.VERIFIED, "Resolution has been verified by the operations team");
        STATE_DESCRIPTIONS.put(VendorTicketStatus.CLOSED, "Ticket has been closed and all related activities are completed");
        STATE_DESCRIPTIONS.put(VendorTicketStatus.ESCALATED, "Ticket has been escalated due to SLA breach or other concerns");
    }

    public void transition(String ticketId, VendorTicketStatus from, VendorTicketStatus to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("State cannot be null for transition");
        }
        if (from == to) {
            throw new IllegalStateException(
                    "Cannot transition ticket " + ticketId + " to the same state: " + from);
        }
        if (!isValidTransition(from, to)) {
            throw new IllegalStateException(
                    "Invalid state transition from " + from + " to " + to + " for ticket " + ticketId);
        }
    }

    public boolean isValidTransition(VendorTicketStatus from, VendorTicketStatus to) {
        if (from == null || to == null) {
            return false;
        }
        Set<VendorTicketStatus> allowed = VALID_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public Set<VendorTicketStatus> getNextStates(VendorTicketStatus current) {
        if (current == null) {
            return Collections.emptySet();
        }
        Set<VendorTicketStatus> nextStates = VALID_TRANSITIONS.get(current);
        if (nextStates == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(nextStates);
    }

    public Set<VendorTicketStatus> getPreviousStates(VendorTicketStatus current) {
        if (current == null) {
            return Collections.emptySet();
        }
        Set<VendorTicketStatus> previous = new LinkedHashSet<>();
        for (Map.Entry<VendorTicketStatus, Set<VendorTicketStatus>> entry : VALID_TRANSITIONS.entrySet()) {
            if (entry.getValue().contains(current)) {
                previous.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(previous);
    }

    public Set<VendorTicketStatus> getAllStates() {
        return Collections.unmodifiableSet(VALID_TRANSITIONS.keySet());
    }

    public String getStateDescription(VendorTicketStatus status) {
        return STATE_DESCRIPTIONS.getOrDefault(status, "Unknown state");
    }

    public Map<VendorTicketStatus, String> getAllStateDescriptions() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(STATE_DESCRIPTIONS));
    }

    public boolean isTerminalState(VendorTicketStatus status) {
        return status == VendorTicketStatus.CLOSED;
    }

    public boolean isErrorState(VendorTicketStatus status) {
        return status == VendorTicketStatus.ESCALATED;
    }

    public List<VendorTicketStatus> getLifecyclePath() {
        return List.of(
                VendorTicketStatus.CREATED,
                VendorTicketStatus.AWAITING_VENDOR,
                VendorTicketStatus.VENDOR_ACKED,
                VendorTicketStatus.VENDOR_IN_PROGRESS,
                VendorTicketStatus.VENDOR_RESOLVED,
                VendorTicketStatus.VERIFIED,
                VendorTicketStatus.CLOSED
        );
    }
}
