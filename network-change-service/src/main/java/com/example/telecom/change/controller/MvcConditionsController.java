package com.example.telecom.change.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Supplementary controller demonstrating the full breadth of Spring MVC
 * mapping conditions: produces, consumes, params, headers, and explicit
 * multi-method mappings. These are additional fixtures; they do
 * not change the primary semantics.
 */
@RestController
@RequestMapping("/api/network-changes")
public class MvcConditionsController {

    /**
     * GET /api/network-changes/{id}/summary produces JSON explicitly.
     */
    @RequestMapping(value = "/{id}/summary",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getSummary(@org.springframework.web.bind.annotation.PathVariable("id") String id) {
        return ResponseEntity.ok(Map.of("id", id, "summary", "ok"));
    }

    /**
     * POST /api/network-changes/import consumes XML only.
     */
    @RequestMapping(value = "/import",
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> importXml() {
        return ResponseEntity.ok("xml-imported");
    }

    /**
     * GET /api/network-changes/filter requires a "region" query parameter.
     */
    @RequestMapping(value = "/filter",
                    method = RequestMethod.GET,
                    params = "region")
    public ResponseEntity<String> filterByRegion(@RequestParam("region") String region) {
        return ResponseEntity.ok("filtered:" + region);
    }

    /**
     * GET /api/network-changes/admin requires header X-Admin=true.
     */
    @RequestMapping(value = "/admin",
                    method = RequestMethod.GET,
                    headers = "X-Admin=true")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("admin-access");
    }

    /**
     * GET+POST /api/network-changes/{id}/toggle accepts both GET and POST
     * via explicit multi-method mapping.
     */
    @RequestMapping(value = "/{id}/toggle",
                    method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> toggle(@org.springframework.web.bind.annotation.PathVariable("id") String id) {
        return ResponseEntity.ok("toggled:" + id);
    }
}
