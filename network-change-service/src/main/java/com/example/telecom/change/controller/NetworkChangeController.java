package com.example.telecom.change.controller;

import com.example.telecom.change.annotation.OpsReadEndpoint;
import com.example.telecom.change.dto.ApprovalRequest;
import com.example.telecom.change.dto.ChangeRequest;
import com.example.telecom.change.dto.ChangeResponse;
import com.example.telecom.change.dto.ChangeSearchRequest;
import com.example.telecom.change.dto.RiskUpdateRequest;
import com.example.telecom.change.service.ChangePlanQueryService;
import com.example.telecom.change.service.ChangePlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Primary network change controller.
 * Class-level {@code @RequestMapping} provides the path prefix "/api/network-changes".
 */
@RestController
@RequestMapping("/api/network-changes")
public class NetworkChangeController {

    private final ChangePlanService planService;
    private final ChangePlanQueryService queryService;

    public NetworkChangeController(ChangePlanService planService,
                                   ChangePlanQueryService queryService) {
        this.planService = planService;
        this.queryService = queryService;
    }

    /** GET /{id} — class prefix + method-level path variable. */
    @GetMapping("/{id}")
    public ResponseEntity<ChangeResponse> getById(@PathVariable("id") String id) {
        return queryService.findById(id)
                .map(plan -> ResponseEntity.ok(new ChangeResponse(
                        plan.getPlanId(), plan.getTitle(), plan.getStatus(),
                        plan.getRegionCode(), plan.getCreatedAt())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/network-changes (empty path + consumes JSON)
     * The empty path="" combined with class prefix yields /api/network-changes.
     */
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody ChangeRequest request) {
        try {
            var plan = planService.createPlan(
                    new com.example.telecom.change.domain.NetworkChangePlan(
                            "CHG-NEW",
                            request.getTitle(),
                            request.getRegionCode(),
                            request.getDeviceFamily(),
                            request.getRisk()));
            return ResponseEntity.status(HttpStatus.CREATED).body(new ChangeResponse(
                    plan.getPlanId(), plan.getTitle(), plan.getStatus(),
                    plan.getRegionCode(), plan.getCreatedAt()));
        } catch (com.example.telecom.change.service.ChangeValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * PUT /api/network-changes/{id}
     */
    @PutMapping(path = "/{id}")
    public ResponseEntity<ChangeResponse> update(@PathVariable("id") String id,
                                                 @RequestBody ChangeRequest request) {
        return ResponseEntity.ok(new ChangeResponse(
                id, request.getTitle(),
                com.example.telecom.change.domain.ChangeStatus.DRAFT,
                request.getRegionCode(),
                java.time.LocalDateTime.now()));
    }

    /**
     * PATCH /api/network-changes/{id}/risk
     * Uses @PatchMapping(value=...) form.
     */
    @PatchMapping(value = "/{id}/risk")
    public ResponseEntity<String> updateRisk(@PathVariable("id") String id,
                                             @RequestBody RiskUpdateRequest request) {
        planService.updateRisk(id, request.getNewRisk());
        return ResponseEntity.ok("risk updated for " + id);
    }

    /**
     * DELETE /api/network-changes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/network-changes/active AND GET /api/network-changes/open
     * Array path — single method maps to multiple URLs.
     */
    @GetMapping({"/active", "/open"})
    public ResponseEntity<List<ChangeResponse>> listActive() {
        var results = queryService.findAll().stream()
                .map(p -> new ChangeResponse(p.getPlanId(), p.getTitle(), p.getStatus(),
                        p.getRegionCode(), p.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/network-changes/pending
     * Path from constant — tests constant resolution in annotations.
     */
    @GetMapping(path = ChangeRoutes.PENDING)
    public ResponseEntity<List<ChangeResponse>> listPending() {
        var results = queryService.findAll().stream()
                .filter(p -> p.getStatus() == com.example.telecom.change.domain.ChangeStatus.PENDING_APPROVAL)
                .map(p -> new ChangeResponse(p.getPlanId(), p.getTitle(), p.getStatus(),
                        p.getRegionCode(), p.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/network-changes
     * Method-level @GetMapping with no path — resolves to class prefix only.
     */
    @GetMapping
    public ResponseEntity<List<ChangeResponse>> listAll() {
        var results = queryService.findAll().stream()
                .map(p -> new ChangeResponse(p.getPlanId(), p.getTitle(), p.getStatus(),
                        p.getRegionCode(), p.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(results);
    }
}
