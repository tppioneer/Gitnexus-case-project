package com.example.telecom.collector;

import com.example.telecom.collector.config.CollectorClock;
import com.example.telecom.collector.config.CollectorProperties;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CollectorPropertiesTest {

    @Test
    void shouldHaveDefaultValues() {
        CollectorProperties props = new CollectorProperties();
        assertEquals(100, props.getMetricBatchSize());
        assertEquals(5000, props.getIngestTimeoutMs());
        assertTrue(props.isValidationEnabled());
    }

    @Test
    void shouldAllowOverridingValues() {
        CollectorProperties props = new CollectorProperties();
        props.setMetricBatchSize(200);
        props.setIngestTimeoutMs(10000);
        props.setValidationEnabled(false);

        assertEquals(200, props.getMetricBatchSize());
        assertEquals(10000, props.getIngestTimeoutMs());
        assertFalse(props.isValidationEnabled());
    }

    @Test
    void collectorClockShouldReturnCurrentTime() {
        CollectorClock clock = new CollectorClock();
        Instant before = Instant.now();
        Instant now = clock.now();
        Instant after = Instant.now();

        assertFalse(now.isBefore(before));
        assertFalse(now.isAfter(after));
    }

    @Test
    void collectorClockShouldReturnCurrentTimeMillis() {
        CollectorClock clock = new CollectorClock();
        long before = System.currentTimeMillis();
        long now = clock.currentTimeMillis();
        long after = System.currentTimeMillis();

        assertTrue(now >= before);
        assertTrue(now <= after);
    }
}
