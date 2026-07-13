package com.example.telecom.dispatch.engine;

import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.common.dispatch.DispatchPriority;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;
import com.example.telecom.dispatch.domain.Operator;
import com.example.telecom.dispatch.rule.DispatchRuleRegistry;
import com.example.telecom.dispatch.service.DispatchScoringService;
import com.example.telecom.dispatch.service.OperatorAvailabilityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DispatchEngine {

    private static final Logger log = LoggerFactory.getLogger(DispatchEngine.class);
    private static final int MAX_OPERATOR_LOAD = 5;

    private final DispatchScoringService scoringService;
    private final OperatorAvailabilityService availabilityService;
    private final DispatchRuleRegistry ruleRegistry;

    public DispatchEngine(DispatchScoringService scoringService,
                          OperatorAvailabilityService availabilityService,
                          DispatchRuleRegistry ruleRegistry) {
        this.scoringService = scoringService;
        this.availabilityService = availabilityService;
        this.ruleRegistry = ruleRegistry;
    }

    public DispatchOrder dispatch(DispatchOrder order) {
        if (!validateOrder(order)) {
            log.warn("Order validation failed for orderId={}", order.getOrderId());
            return DispatchOrder.builder()
                    .orderId(order.getOrderId())
                    .workOrderId(order.getWorkOrderId())
                    .priority(order.getPriority())
                    .targetRegionCode(order.getTargetRegionCode())
                    .assigneeId(order.getAssigneeId())
                    .skillRequired(order.getSkillRequired())
                    .estimatedDuration(order.getEstimatedDuration())
                    .status("VALIDATION_FAILED")
                    .createdTime(order.getCreatedTime())
                    .assignedTime(order.getAssignedTime())
                    .completedTime(order.getCompletedTime())
                    .build();
        }

        List<DispatchRuleResult> ruleResults = evaluateRules(order);
        boolean rulesPassed = ruleResults.stream().allMatch(DispatchRuleResult::isPassed);
        if (!rulesPassed) {
            log.warn("Dispatch rules failed for orderId={}, rules={}", order.getOrderId(),
                    ruleResults.stream().filter(r -> !r.isPassed()).map(DispatchRuleResult::getRuleName).collect(Collectors.toList()));
        }

        List<String> candidates = findCandidates(order);
        if (candidates.isEmpty()) {
            log.warn("No candidates found for orderId={}", order.getOrderId());
            return DispatchOrder.builder()
                    .orderId(order.getOrderId())
                    .workOrderId(order.getWorkOrderId())
                    .priority(order.getPriority())
                    .targetRegionCode(order.getTargetRegionCode())
                    .assigneeId(order.getAssigneeId())
                    .skillRequired(order.getSkillRequired())
                    .estimatedDuration(order.getEstimatedDuration())
                    .status("FAILED")
                    .createdTime(order.getCreatedTime())
                    .assignedTime(order.getAssignedTime())
                    .completedTime(order.getCompletedTime())
                    .build();
        }
        Map<String, Double> scores = scoreCandidates(order, candidates);
        String bestCandidate = selectBestCandidate(scores);
        log.info("Dispatch orderId={} assigned to candidate={} with score={}",
                order.getOrderId(), bestCandidate, scores.get(bestCandidate));
        return executeAssignment(order, bestCandidate);
    }

    public List<String> findCandidates(DispatchOrder order) {
        List<Operator> operators = availabilityService.getAvailableOperators();
        List<String> candidates = new ArrayList<>();
        for (Operator op : operators) {
            double skillScore = scoringService.calculateSkillScore(op.getId(), order.getSkillRequired());
            if (skillScore <= 0) {
                continue;
            }
            // Granular skill filtering: prefer operators with a meaningful skill match
            if (skillScore < 0.5) {
                continue;
            }
            double proximityScore = scoringService.calculateProximityScore(op.getId(), order.getTargetRegionCode());
            if (proximityScore <= 0) {
                continue;
            }
            int load = availabilityService.getOperatorLoad(op.getId());
            if (load >= 5) {
                continue;
            }
            // Verify operator availability score is positive
            double availabilityScore = scoringService.calculateAvailabilityScore(op.getId());
            if (availabilityScore <= 0) {
                continue;
            }
            candidates.add(op.getId());
        }
        return candidates;
    }

    public Map<String, Double> scoreCandidates(DispatchOrder order, List<String> candidateIds) {
        List<Operator> operators = availabilityService.getAvailableOperators();
        Map<String, Operator> operatorMap = new HashMap<>();
        for (Operator op : operators) {
            operatorMap.put(op.getId(), op);
        }

        Map<String, Double> scores = new HashMap<>();
        for (String id : candidateIds) {
            Operator op = operatorMap.get(id);
            if (op != null) {
                double score = scoringService.calculateScore(order, op);
                scores.put(id, score);
            }
        }
        return scores;
    }

    public String selectBestCandidate(Map<String, Double> scores) {
        String best = null;
        double maxScore = Double.MIN_VALUE;
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                best = entry.getKey();
            }
        }
        if (best == null) {
            throw new DomainException("NO_CANDIDATE", "No candidate found for assignment");
        }
        return best;
    }

    public DispatchOrder executeAssignment(DispatchOrder order, String assigneeId) {
        Map<String, Double> scoreComponents = getAssignmentScore(assigneeId, order);
        log.info("Assigning order {} to operator {} with skillScore={}, loadScore={}, proximityScore={}, availabilityScore={}",
                order.getOrderId(), assigneeId,
                scoreComponents.get("skill"),
                scoreComponents.get("load"),
                scoreComponents.get("proximity"),
                scoreComponents.get("availability"));
        return DispatchOrder.builder()
                .orderId(order.getOrderId())
                .workOrderId(order.getWorkOrderId())
                .priority(order.getPriority())
                .targetRegionCode(order.getTargetRegionCode())
                .assigneeId(assigneeId)
                .skillRequired(order.getSkillRequired())
                .estimatedDuration(order.getEstimatedDuration())
                .status("ASSIGNED")
                .createdTime(order.getCreatedTime())
                .assignedTime(LocalDateTime.now())
                .completedTime(order.getCompletedTime())
                .build();
    }

    public boolean validateOrder(DispatchOrder order) {
        if (order == null) {
            log.warn("Order is null");
            return false;
        }
        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            log.warn("Order ID is null or empty");
            return false;
        }
        if (order.getPriority() == null) {
            log.warn("Order priority is null for orderId={}", order.getOrderId());
            return false;
        }
        if (order.getTargetRegionCode() == null || order.getTargetRegionCode().isEmpty()) {
            log.warn("Target region code is null or empty for orderId={}", order.getOrderId());
            return false;
        }
        if (order.getSkillRequired() == null || order.getSkillRequired().isEmpty()) {
            log.warn("Skill required is null or empty for orderId={}", order.getOrderId());
            return false;
        }
        return true;
    }

    public List<DispatchRuleResult> evaluateRules(DispatchOrder order) {
        if (order == null) {
            return Collections.emptyList();
        }
        List<Operator> operators = availabilityService.getAvailableOperators();
        List<String> availableOperatorIds = operators.stream()
                .map(Operator::getId)
                .collect(Collectors.toList());

        DispatchContext context = new DispatchContext(
                order,
                availableOperatorIds,
                order.getTargetRegionCode(),
                order.getPriority(),
                order.getSkillRequired() != null
                        ? Collections.singletonList(order.getSkillRequired())
                        : Collections.emptyList(),
                LocalDateTime.now()
        );

        return ruleRegistry.evaluateAll(context);
    }

    public Map<String, Double> getAssignmentScore(String operatorId, DispatchOrder order) {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        if (operatorId == null || order == null) {
            breakdown.put("skill", 0.0);
            breakdown.put("load", 0.0);
            breakdown.put("proximity", 0.0);
            breakdown.put("availability", 0.0);
            breakdown.put("total", 0.0);
            return breakdown;
        }

        double skillScore = scoringService.calculateSkillScore(operatorId, order.getSkillRequired());
        double loadScore = scoringService.calculateLoadScore(operatorId);
        double proximityScore = scoringService.calculateProximityScore(operatorId, order.getTargetRegionCode());
        double availabilityScore = scoringService.calculateAvailabilityScore(operatorId);
        double total = skillScore * 0.4 + loadScore * 0.3 + proximityScore * 0.2 + availabilityScore * 0.1;

        breakdown.put("skill", skillScore);
        breakdown.put("load", loadScore);
        breakdown.put("proximity", proximityScore);
        breakdown.put("availability", availabilityScore);
        breakdown.put("total", total);
        return breakdown;
    }

    public List<String> findOperatorsBySkill(String skill) {
        if (skill == null || skill.isEmpty()) {
            return Collections.emptyList();
        }
        List<Operator> operators = availabilityService.getAvailableOperators();
        List<String> matched = new ArrayList<>();
        for (Operator op : operators) {
            double skillScore = scoringService.calculateSkillScore(op.getId(), skill);
            if (skillScore > 0) {
                matched.add(op.getId());
            }
        }
        return matched;
    }

    public int getAvailableOperatorsCount() {
        return availabilityService.getAvailableOperators().size();
    }

    public Map<String, Map<String, Double>> getCandidateDetails(DispatchOrder order) {
        if (order == null) {
            return Collections.emptyMap();
        }
        List<String> candidates = findCandidates(order);
        Map<String, Map<String, Double>> details = new LinkedHashMap<>();
        for (String candidate : candidates) {
            details.put(candidate, getAssignmentScore(candidate, order));
        }
        return details;
    }

    public List<String> rankCandidates(DispatchOrder order) {
        if (order == null) {
            return Collections.emptyList();
        }
        List<String> candidates = findCandidates(order);
        Map<String, Double> scores = scoreCandidates(order, candidates);
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<DispatchOrder> batchAssign(List<DispatchOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }
        List<DispatchOrder> results = new ArrayList<>();
        for (DispatchOrder order : orders) {
            results.add(dispatch(order));
        }
        return results;
    }

    public Optional<DispatchOrder> findAndDispatch(String workOrderId) {
        DispatchOrder order = DispatchOrder.builder()
                .orderId(java.util.UUID.randomUUID().toString())
                .workOrderId(workOrderId)
                .priority(DispatchPriority.MEDIUM)
                .status("PENDING")
                .createdTime(LocalDateTime.now())
                .build();
        DispatchOrder result = dispatch(order);
        if ("ASSIGNED".equals(result.getStatus())) {
            return Optional.of(result);
        }
        return Optional.empty();
    }
}
