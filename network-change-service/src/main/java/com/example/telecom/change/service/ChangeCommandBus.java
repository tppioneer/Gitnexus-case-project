package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeCommand;
import com.example.telecom.change.domain.ChangeMode;
import com.example.telecom.change.domain.DispatchResult;
import com.example.telecom.change.domain.EmergencyChangeCommand;
import org.springframework.stereotype.Service;

/**
 * Command bus with overloaded dispatch methods.
 * Three distinct methods named "dispatch" with different parameter types.
 * A fourth overload demonstrates primitive-vs-boxed resolution.
 */
@Service
public class ChangeCommandBus {

    /** Dispatch a base ChangeCommand. */
    public DispatchResult dispatch(ChangeCommand command) {
        return DispatchResult.accepted("ChangeCommand",
                "Dispatched base command: " + command.getChangeId());
    }

    /** Dispatch an EmergencyChangeCommand — overload #2. */
    public DispatchResult dispatch(EmergencyChangeCommand command) {
        return DispatchResult.accepted("EmergencyChangeCommand",
                "Dispatched emergency: " + command.getChangeId() +
                " severity=" + command.getSeverity());
    }

    /** Dispatch by changeId and mode — overload #3. */
    public DispatchResult dispatch(String changeId, ChangeMode mode) {
        return DispatchResult.accepted("ChangeId+Mode",
                "Dispatched " + changeId + " in mode " + mode);
    }

    /**
     * Overload resolution: {@code int} vs {@code Integer}. Callers passing a
     * primitive {@code int} bind here; callers passing {@code Integer} or
     * unboxing to int bind to the {@code Integer} overload.
     */
    public DispatchResult dispatch(int retryCount) {
        return DispatchResult.accepted("retryCount:primitive",
                "Primitive int dispatch, count=" + retryCount);
    }

    /** Boxed overload — distinguishable at the source level. */
    public DispatchResult dispatch(Integer retryCount) {
        return DispatchResult.accepted("retryCount:boxed",
                "Boxed Integer dispatch, count=" + retryCount);
    }

    /**
     * Production call site demonstrating overload resolution.
     * All three main dispatch overloads are exercised here, alongside the
     * lambda forEach callback and method reference patterns.
     */
    public DispatchResult dispatchAll(ChangeCommand base, EmergencyChangeCommand emergency,
                                       String changeId, ChangeMode mode) {
        DispatchResult r1 = this.dispatch((ChangeCommand) base);
        DispatchResult r2 = this.dispatch(emergency);
        DispatchResult r3 = this.dispatch(changeId, mode);
        return DispatchResult.accepted("dispatchAll",
                r1.getDispatchedTo() + "," + r2.getDispatchedTo() + "," + r3.getDispatchedTo());
    }
}
