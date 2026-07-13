package com.example.telecom.common.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads JSON test fixtures from the classpath and deserialises them into Java
 * objects.
 *
 * <p>Parsed objects are cached so repeated requests for the same fixture return
 * the same instance (or a copy, depending on the use case).  The cache can be
 * cleared with {@link #clearCache()}.
 *
 * <p>The underlying {@link ObjectMapper} is configured with the JSR-310 module
 * for {@code java.time} types and lenient enum deserialisation.
 */
public final class JsonFixtureLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);

    private static final Map<String, Object> cache = new ConcurrentHashMap<>();

    private JsonFixtureLoader() {
        // utility class
    }

    /**
     * Loads a single JSON object from the classpath and deserialises it to the
     * given type.
     *
     * @param path  classpath resource path (e.g. {@code /fixtures/device.json})
     * @param clazz target type
     * @param <T>   the target type
     * @return deserialised instance
     * @throws IllegalArgumentException if the resource cannot be found or parsed
     */
    @SuppressWarnings("unchecked")
    public static <T> T loadFixture(final String path, final Class<T> clazz) {
        return (T) cache.computeIfAbsent(path, key -> {
            try (final InputStream is = openStream(key)) {
                return MAPPER.readValue(is, clazz);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Failed to load fixture: " + key, e);
            }
        });
    }

    /**
     * Loads a JSON array from the classpath and deserialises it to a list of the
     * given element type.
     *
     * @param path  classpath resource path (e.g. {@code /fixtures/devices.json})
     * @param clazz element type
     * @param <T>   the element type
     * @return list of deserialised instances
     * @throws IllegalArgumentException if the resource cannot be found or parsed
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadFixtureList(final String path, final Class<T> clazz) {
        return (List<T>) cache.computeIfAbsent(path, key -> {
            try (final InputStream is = openStream(key)) {
                return MAPPER.readValue(is, new TypeReference<List<T>>() {
                });
            } catch (final IOException e) {
                throw new IllegalArgumentException("Failed to load fixture list: " + key, e);
            }
        });
    }

    /**
     * Clears the internal fixture cache.  Subsequent calls to {@link #loadFixture}
     * or {@link #loadFixtureList} will re-read and re-parse the resource files.
     */
    public static void clearCache() {
        cache.clear();
    }

    private static InputStream openStream(final String path) {
        final InputStream is = JsonFixtureLoader.class.getResourceAsStream(path);
        if (is == null) {
            throw new IllegalArgumentException("Fixture resource not found on classpath: " + path);
        }
        return is;
    }
}
