package com.example.telecom.common.test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * A test-friendly clock provider that allows callers to pin the current time
 * to a fixed instant.
 *
 * <p>Use {@link #setFixed(Instant, ZoneId)} or
 * {@link #setFixed(LocalDateTime, ZoneId)} to freeze time, and {@link #reset()}
 * to restore the system clock.
 *
 * <p>All {@code now...()} methods delegate to the current {@link Clock} so they
 * automatically reflect whatever clock is active (system or fixed).
 */
public final class TestClockProvider {

    private static Clock clock = Clock.systemDefaultZone();

    private TestClockProvider() {
        // utility class
    }

    /**
     * Pins the clock to a fixed instant in the given time-zone.
     *
     * @param instant the fixed instant
     * @param zone    the time-zone
     */
    public static void setFixed(final Instant instant, final ZoneId zone) {
        clock = Clock.fixed(instant, zone);
    }

    /**
     * Pins the clock to a fixed local date-time interpreted in the given time-zone.
     *
     * @param dateTime the local date-time to freeze at
     * @param zone     the time-zone
     */
    public static void setFixed(final LocalDateTime dateTime, final ZoneId zone) {
        final Instant instant = dateTime.atZone(zone).toInstant();
        clock = Clock.fixed(instant, zone);
    }

    /**
     * Resets the clock to the system default time-zone clock.
     */
    public static void reset() {
        clock = Clock.systemDefaultZone();
    }

    /**
     * Returns the current instant from the active clock.
     *
     * @return current instant
     */
    public static Instant now() {
        return Instant.now(clock);
    }

    /**
     * Returns the current local date-time from the active clock.
     *
     * @return current local date-time
     */
    public static LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now(clock);
    }

    /**
     * Returns the current zoned date-time from the active clock.
     *
     * @return current zoned date-time
     */
    public static ZonedDateTime nowZonedDateTime() {
        return ZonedDateTime.now(clock);
    }

    /**
     * Returns the underlying {@link Clock} instance.
     *
     * @return the active clock
     */
    public static Clock getClock() {
        return clock;
    }
}
