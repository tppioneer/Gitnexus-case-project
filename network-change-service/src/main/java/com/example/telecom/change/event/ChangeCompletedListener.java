package com.example.telecom.change.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Listener for {@code ChangeCompletedEvent} bound to AFTER_COMMIT phase.
 * This event is only delivered after the enclosing transaction commits —
 * tests must use a real transaction (not direct listener invocation).
 */
@Component
public class ChangeCompletedListener {

    private final List<ChangeCompletedEvent> received = new ArrayList<>();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(ChangeCompletedEvent event) {
        received.add(event);
    }

    public List<ChangeCompletedEvent> received() {
        return Collections.unmodifiableList(received);
    }

    public int count() {
        return received.size();
    }

    public void clear() {
        received.clear();
    }
}
