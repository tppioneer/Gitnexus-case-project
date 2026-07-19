package com.example.telecom.change.controller;

import com.example.telecom.change.annotation.OpsReadEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating composed Spring mapping annotations.
 * This route is produced solely by the composed annotation
 * {@code @OpsReadEndpoint} — no backup {@code @GetMapping} is present.
 */
@RestController
@RequestMapping("/api/network-changes")
public class ComposedEndpointController {

    /**
     * GET /api/network-changes/{id}/audit
     * The route comes exclusively from the composed {@code @OpsReadEndpoint},
     * which carries {@code @RequestMapping(method = GET)} as its meta-annotation
     * and exposes the path through {@code @AliasFor}.
     */
    @OpsReadEndpoint(path = "/{id}/audit")
    public ResponseEntity<List<Map<String, String>>> getAuditTrail(@PathVariable("id") String id) {
        return ResponseEntity.ok(List.of(
                Map.of("action", "created", "by", "system"),
                Map.of("action", "approved", "by", "operator-1")
        ));
    }

    /** Noise method with same name but no mapping annotation. */
    public String getAuditTrail(String id, String format) {
        return "noise-audit-" + id + "-" + format;
    }
}
