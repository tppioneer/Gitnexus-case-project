package com.example.telecom.common.test;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods for constructing parameterised test inputs.
 *
 * <p>Provides combinatorial helpers such as Cartesian products, enum value
 * streams, ordered pairs, and tagged valid/invalid input streams.
 */
public final class ParameterizedTestUtils {

    private ParameterizedTestUtils() {
        // utility class
    }

    /**
     * Computes the Cartesian product of the given lists.
     *
     * <p>Each element of the result is a list whose size equals the number of
     * input lists, containing one element from each input list in order.
     *
     * @param lists one or more lists whose Cartesian product to compute
     * @param <T>   common base type of all elements
     * @return list of all combinations
     */
    public static <T> List<List<T>> combinations(final List<List<T>> lists) {
        final List<List<T>> result = new ArrayList<>();
        if (lists.isEmpty()) {
            result.add(List.of());
            return result;
        }
        combinationsRecursive(lists, 0, new ArrayList<>(), result);
        return result;
    }

    private static <T> void combinationsRecursive(final List<List<T>> lists, final int depth,
                                                  final List<T> current, final List<List<T>> result) {
        if (depth == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (final T element : lists.get(depth)) {
            current.add(element);
            combinationsRecursive(lists, depth + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Returns a stream of all values in the given enum type.
     *
     * @param enumClass the enum class
     * @param <E>       the enum type
     * @return a stream of all enum constants
     */
    public static <E extends Enum<E>> Stream<E> enumValues(final Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants());
    }

    /**
     * Returns all ordered pairs from the given list (including self-pairs).
     *
     * <p>The result contains {@code n^2} entries where {@code n} is the size of
     * the input list.  Each entry is a {@link Map.Entry} with {@code (first, second)}.
     *
     * @param items the items to pair
     * @param <T>   the item type
     * @return list of ordered pairs
     */
    public static <T> List<Map.Entry<T, T>> pairs(final List<T> items) {
        final List<Map.Entry<T, T>> result = new ArrayList<>();
        for (final T first : items) {
            for (final T second : items) {
                result.add(new SimpleImmutableEntry<>(first, second));
            }
        }
        return result;
    }

    /**
     * Produces a stream of "tagged" inputs that can be used with JUnit 5
     * {@code @MethodSource}.  Valid inputs are tagged with {@code "valid"} and
     * invalid inputs with {@code "invalid"}.
     *
     * <p>The tag is embedded as the first element of each {@code Object[]},
     * making it suitable for use as a test display name.
     *
     * @param valid   stream of valid inputs
     * @param invalid stream of invalid inputs
     * @param <T>     the input type
     * @return stream of {@code Object[]} where {@code [0]} is the tag and
     *         {@code [1]} is the value
     */
    public static <T> Stream<Object[]> validAndInvalidInputs(final Stream<T> valid, final Stream<T> invalid) {
        return Stream.concat(
                valid.map(v -> new Object[]{"valid", v}),
                invalid.map(v -> new Object[]{"invalid", v})
        );
    }
}
