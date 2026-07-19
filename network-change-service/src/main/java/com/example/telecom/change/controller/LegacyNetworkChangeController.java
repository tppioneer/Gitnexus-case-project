package com.example.telecom.change.controller;

import com.example.telecom.change.dto.ChangeSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Legacy controller — no class-level prefix. Demonstrates method-level
 * @RequestMapping with various configurations.
 */
@RestController
public class LegacyNetworkChangeController {

    /**
     * ANY /legacy/preview
     * Method-level @RequestMapping without specifying method —
     * accepts ANY HTTP method.
     */
    @RequestMapping("/legacy/preview")
    public ResponseEntity<String> preview() {
        return ResponseEntity.ok("preview endpoint — accepts any HTTP method");
    }

    /**
     * GET /legacy/search
     * Method-level @RequestMapping with explicit method=RequestMethod.GET.
     * This legacy endpoint uses the explicit method attribute.
     */
    @RequestMapping(path = "/legacy/search", method = RequestMethod.GET)
    public ResponseEntity<String> search(@RequestParam(value = "q", required = false) String query) {
        return ResponseEntity.ok("search results for: " + query);
    }

    /**
     * GET /qualified
     * Uses fully-qualified annotation name — tests that tools resolve
     * FQN to the same mapping as the short name.
     */
    @org.springframework.web.bind.annotation.GetMapping("/qualified")
    public ResponseEntity<String> qualifiedEndpoint() {
        return ResponseEntity.ok("fully-qualified mapping endpoint");
    }

    /**
     * Noise: same name "search" but different method — not a route conflict.
     * This method has no mapping annotation.
     */
    public List<String> search(String regionCode, String status) {
        return List.of("noise-search-" + regionCode + "-" + status);
    }
}
