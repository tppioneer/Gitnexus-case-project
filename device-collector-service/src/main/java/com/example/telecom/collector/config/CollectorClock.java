package com.example.telecom.collector.config;

import java.time.Instant;

/**
 * Time abstraction utility for testability.
 */
public class CollectorClock {

    public Instant now() {
        return Instant.now();
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
