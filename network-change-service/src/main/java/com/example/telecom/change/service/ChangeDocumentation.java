package com.example.telecom.change.service;

import java.util.List;

/**
 * Plain Java class that returns documentation strings describing change
 * routes. Has no Spring MVC annotations — none of its string contents
 * are actual HTTP mappings.
 */
public class ChangeDocumentation {

    /** Returns example mapping strings for documentation — plain strings, not mappings. */
    public List<String> getMappingExamples() {
        return List.of(
                "/api/network-changes — list all changes",
                "/api/network-changes/{id} — get change by id",
                "/api/network-changes/active — list active changes",
                "@GetMapping(\"/comment-only\") — this is just a string, not an annotation"
        );
    }

    /** Describes a route for display — the parameter is a plain string. */
    public String describe(String path) {
        return "Documentation for path: " + path;
    }

    /** Returns a sample log message; the path referenced does not correspond to a real endpoint. */
    public String getFakeRouteLogMessage() {
        return "GET /api/network-changes/fake — this path does not exist";
    }
}
