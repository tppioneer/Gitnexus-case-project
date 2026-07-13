package com.example.telecom.common.test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility methods for generating and working with time ranges in tests.
 */
public final class TimeRangeTestUtils {

    private static final DateTimeFormatter DURATION_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private TimeRangeTestUtils() {
        // utility class
    }

    /**
     * Generates time slots for an entire day at the given interval.
     *
     * <p>The first slot starts at {@code 00:00} and the last slot ends at
     * {@code 23:59}.  Each slot is represented as a start/end pair.
     *
     * @param date            the date for which to generate slots
     * @param intervalMinutes the duration of each slot in minutes
     * @return list of start/end pairs
     * @throws IllegalArgumentException if intervalMinutes is not positive
     */
    public static List<Slot> generateTimeSlots(final LocalDate date, final int intervalMinutes) {
        if (intervalMinutes <= 0) {
            throw new IllegalArgumentException("intervalMinutes must be positive, got: " + intervalMinutes);
        }
        final List<Slot> slots = new ArrayList<>();
        LocalDateTime cursor = date.atStartOfDay();
        final LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        while (cursor.isBefore(endOfDay)) {
            final LocalDateTime slotEnd = cursor.plusMinutes(intervalMinutes);
            final LocalDateTime actualEnd = slotEnd.isAfter(endOfDay) ? endOfDay : slotEnd;
            slots.add(new Slot(cursor, actualEnd));
            cursor = actualEnd;
        }
        return slots;
    }

    /**
     * Checks whether two time ranges overlap.
     *
     * @param start1 start of the first range (inclusive)
     * @param end1   end of the first range (exclusive)
     * @param start2 start of the second range (inclusive)
     * @param end2   end of the second range (exclusive)
     * @return {@code true} if the ranges overlap
     */
    public static boolean isOverlapping(final LocalDateTime start1, final LocalDateTime end1,
                                        final LocalDateTime start2, final LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Returns a random {@link LocalDateTime} within the given range (inclusive
     * on both sides).
     *
     * @param start the inclusive start
     * @param end   the inclusive end
     * @return a random date-time between start and end
     * @throws IllegalArgumentException if start is after end
     */
    public static LocalDateTime randomTimeInRange(final LocalDateTime start, final LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must not be after end: " + start + " > " + end);
        }
        final long seconds = Duration.between(start, end).getSeconds();
        if (seconds == 0) {
            return start;
        }
        final long randomOffset = ThreadLocalRandom.current().nextLong(seconds + 1);
        return start.plusSeconds(randomOffset);
    }

    /**
     * Returns a human-readable representation of the time between two instants.
     *
     * <p>The format is {@code HH:mm:ss} for durations under 24 hours, or
     * {@code X days, HH:mm:ss} for longer periods.
     *
     * @param from the start instant
     * @param to   the end instant
     * @return a friendly duration string
     */
    public static String timeUntil(final LocalDateTime from, final LocalDateTime to) {
        final Duration duration = Duration.between(from, to);
        final long days = duration.toDays();
        final long hours = duration.toHours() % 24;
        final long minutes = duration.toMinutes() % 60;
        final long seconds = duration.getSeconds() % 60;

        final String timePart = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return days > 0 ? days + " days, " + timePart : timePart;
    }

    /**
     * A simple value object representing a time slot with a start and end.
     *
     * @param start slot start (inclusive)
     * @param end   slot end (exclusive)
     */
    public record Slot(LocalDateTime start, LocalDateTime end) {
    }
}
