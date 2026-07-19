package com.example.telecom.change.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Listener for {@code ChangeApprovedEvent}. Uses the standard
 * {@code @EventListener} annotation.
 */
@Component
public class ChangeApprovedListener {

    private final List<ChangeApprovedEvent> received = new ArrayList<>();

    @EventListener
    public void onApproved(ChangeApprovedEvent event) {
        received.add(event);
    }

    public List<ChangeApprovedEvent> received() {
        return Collections.unmodifiableList(received);
    }

    public int count() {
        return received.size();
    }

    public void clear() {
        received.clear();
    }
}
