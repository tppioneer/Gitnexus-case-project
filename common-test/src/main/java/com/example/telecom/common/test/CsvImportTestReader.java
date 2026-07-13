package com.example.telecom.common.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads CSV test data from classpath resources and returns each row as a
 * header-to-value map.
 *
 * <p>Supports quoted fields (including embedded commas and newlines within
 * quotes) and skips blank lines.
 */
public final class CsvImportTestReader {

    private CsvImportTestReader() {
        // utility class
    }

    /**
     * Reads a CSV file from the classpath and returns its contents as a list of
     * maps.  The first non-empty line is treated as the header row; all subsequent
     * non-empty lines are data rows.
     *
     * @param resourcePath classpath resource path (e.g. {@code /testdata/devices.csv})
     * @return list of rows, each represented as an ordered header-to-value map
     * @throws IllegalArgumentException if the resource cannot be found or read
     */
    public static List<Map<String, String>> readCsv(final String resourcePath) {
        final List<String[]> rows = new ArrayList<>();

        try (final InputStream is = openStream(resourcePath);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                rows.add(parseCsvLine(line));
            }
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to read CSV resource: " + resourcePath, e);
        }

        if (rows.isEmpty()) {
            return List.of();
        }

        final String[] headers = rows.get(0);
        final List<Map<String, String>> result = new ArrayList<>(rows.size() - 1);

        for (int i = 1; i < rows.size(); i++) {
            final String[] values = rows.get(i);
            final Map<String, String> rowMap = new LinkedHashMap<>();
            for (int j = 0; j < headers.length; j++) {
                final String value = j < values.length ? values[j] : "";
                rowMap.put(headers[j], value);
            }
            result.add(rowMap);
        }

        return result;
    }

    /**
     * Parses a single CSV line, handling quoted fields.
     *
     * @param line the raw CSV line
     * @return array of field values
     */
    private static String[] parseCsvLine(final String line) {
        final List<String> fields = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            final char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // escaped quote inside a quoted field
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());

        return fields.toArray(new String[0]);
    }

    private static InputStream openStream(final String path) {
        final InputStream is = CsvImportTestReader.class.getResourceAsStream(path);
        if (is == null) {
            throw new IllegalArgumentException("CSV resource not found on classpath: " + path);
        }
        return is;
    }
}
