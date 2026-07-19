package com.example.telecom.change.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Async listener for {@code ChangeCompletedEvent}. Marked with {@code @Async}
 * to exercise Spring's async event dispatch.
 */
@Component
public class ChangeNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(ChangeNotificationListener.class);

    private final List<ChangeCompletedEvent> received = new ArrayList<>();
    private volatile CountDownLatch latch = new CountDownLatch(1);

    @Async
    @EventListener
    public void notifyAsync(ChangeCompletedEvent event) {
        log.info("Async notification for change {}", event.getPlanId());
        synchronized (received) {
            received.add(event);
        }
        latch.countDown();
    }

    public List<ChangeCompletedEvent> received() {
        synchronized (received) {
            return Collections.unmodifiableList(new ArrayList<>(received));
        }
    }

    public int count() {
        synchronized (received) {
            return received.size();
        }
    }

    public void resetLatch(int count) {
        this.latch = new CountDownLatch(count);
    }

    public boolean awaitNotification(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void clear() {
        synchronized (received) {
            received.clear();
        }
        latch = new CountDownLatch(1);
    }
}
