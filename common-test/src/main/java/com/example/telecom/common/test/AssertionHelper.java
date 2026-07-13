package com.example.telecom.common.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Custom assertion helpers for common test scenarios in the telecom platform.
 *
 * <p>Each method throws {@link AssertionError} on failure, consistent with
 * JUnit 5's assertion contract.
 */
public final class AssertionHelper {

    private AssertionHelper() {
        // utility class
    }

    /**
     * Asserts that executing the given lambda throws an exception of the expected
     * type <em>and</em> that its message contains the given substring.
     *
     * @param exType            the expected exception type
     * @param exec              the executable to invoke
     * @param messageSubstring  text that must appear in the exception message
     * @param <E>               the expected exception type
     * @return the caught exception for further inspection
     */
    public static <E extends Throwable> E assertThrowsWithMessage(
            final Class<E> exType,
            final Executable exec,
            final String messageSubstring) {

        final E ex = assertThrows(exType, exec::execute);
        assertNotNull(ex.getMessage(), "Exception message must not be null");
        assertTrue(ex.getMessage().contains(messageSubstring),
                () -> "Expected exception message to contain '" + messageSubstring
                        + "' but was: " + ex.getMessage());
        return ex;
    }

    /**
     * Asserts that {@code actual} is within {@code tolerance} of {@code expected}.
     *
     * @param actual    the actual value
     * @param expected  the expected value
     * @param tolerance the maximum allowed difference
     */
    public static void assertWithinTolerance(final double actual, final double expected, final double tolerance) {
        final double diff = Math.abs(actual - expected);
        assertTrue(diff <= tolerance,
                () -> "Expected " + expected + " +/- " + tolerance + " but got " + actual
                        + " (difference: " + diff + ")");
    }

    /**
     * Asserts that the given {@link Optional} is non-empty and returns its value.
     *
     * @param optional the optional to check
     * @param message  failure message displayed if the optional is empty
     * @param <T>      the value type
     * @return the unwrapped value
     */
    public static <T> T assertNotEmpty(final Optional<? extends T> optional, final String message) {
        assertTrue(optional.isPresent(), message);
        return optional.get();
    }

    /**
     * Asserts that a collection contains exactly the expected number of elements.
     *
     * @param collection   the iterable collection
     * @param expectedSize the expected number of elements
     */
    public static void assertCollectionSize(final Iterable<?> collection, final int expectedSize) {
        int actualSize = 0;
        for (final Object ignored : collection) {
            actualSize++;
        }
        assertEquals(expectedSize, actualSize,
                "Expected collection size " + expectedSize + " but got " + actualSize);
    }

    /**
     * Asserts that the items in the list are in their natural order (non-decreasing).
     *
     * @param items the list to check; each element must implement {@link Comparable}
     * @param <T>   the element type
     */
    public static <T extends Comparable<? super T>> void assertOrdered(final List<T> items) {
        final String msg = IntStream.range(1, items.size())
                .filter(i -> items.get(i - 1).compareTo(items.get(i)) > 0)
                .mapToObj(i -> "items[" + (i - 1) + "] (" + items.get(i - 1)
                        + ") > items[" + i + "] (" + items.get(i) + ")")
                .collect(Collectors.joining("; "));
        assertTrue(msg.isEmpty(), "List is not in natural order: " + msg);
    }

    /**
     * Asserts that a {@link LocalDateTime} value falls within the given range
     * (inclusive on both sides).
     *
     * @param value the value to check
     * @param start the inclusive start of the range
     * @param end   the inclusive end of the range
     */
    public static void assertBetween(final LocalDateTime value, final LocalDateTime start, final LocalDateTime end) {
        assertNotNull(value, "Value must not be null");
        assertNotNull(start, "Start must not be null");
        assertNotNull(end, "End must not be null");
        assertTrue(!value.isBefore(start) && !value.isAfter(end),
                () -> "Expected " + value + " to be between " + start + " and " + end);
    }

    /**
     * Functional interface for a block of code that may throw an exception.
     * Mirrors JUnit 5's {@code Executable} but avoids requiring the import.
     */
    @FunctionalInterface
    public interface Executable {
        void execute() throws Throwable;
    }
}
