package com.example.telecom.dispatch.service;

import com.example.telecom.common.api.PagedResult;
import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.common.dispatch.DispatchPriority;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.dispatch.domain.DispatchHistory;
import com.example.telecom.dispatch.domain.Operator;
import com.example.telecom.dispatch.dto.DispatchResponse;
import com.example.telecom.dispatch.dto.DispatchSummaryResponse;
import com.example.telecom.dispatch.engine.DispatchEngine;
import com.example.telecom.dispatch.engine.DispatchOptimizer;
import com.example.telecom.dispatch.event.DispatchEventPublisher;
import com.example.telecom.dispatch.mapper.DispatchMapper;
import com.example.telecom.dispatch.repository.DispatchHistoryRepository;
import com.example.telecom.dispatch.repository.DispatchRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DispatchOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(DispatchOrchestrationService.class);

    private final DispatchRepository dispatchRepository;
    private final DispatchHistoryRepository dispatchHistoryRepository;
    private final DispatchEngine dispatchEngine;
    private final DispatchOptimizer dispatchOptimizer;
    private final DispatchEventPublisher dispatchEventPublisher;
    private final OperatorAvailabilityService operatorAvailabilityService;
    private final DispatchScoringService dispatchScoringService;
    private final DispatchMapper dispatchMapper;

    public DispatchOrchestrationService(DispatchRepository dispatchRepository,
                                         DispatchHistoryRepository dispatchHistoryRepository,
                                         DispatchEngine dispatchEngine,
                                         DispatchOptimizer dispatchOptimizer,
                                         DispatchEventPublisher dispatchEventPublisher,
                                         OperatorAvailabilityService operatorAvailabilityService,
                                         DispatchScoringService dispatchScoringService,
                                         DispatchMapper dispatchMapper) {
        this.dispatchRepository = dispatchRepository;
        this.dispatchHistoryRepository = dispatchHistoryRepository;
        this.dispatchEngine = dispatchEngine;
        this.dispatchOptimizer = dispatchOptimizer;
        this.dispatchEventPublisher = dispatchEventPublisher;
        this.operatorAvailabilityService = operatorAvailabilityService;
        this.dispatchScoringService = dispatchScoringService;
        this.dispatchMapper = dispatchMapper;
    }

    public DispatchResponse dispatch(String workOrderId) {
        log.info("Dispatching work order: {}", workOrderId);

        DispatchOrder order = DispatchOrder.builder()
                .orderId(UUID.randomUUID().toString())
                .workOrderId(workOrderId)
                .priority(DispatchPriority.MEDIUM)
                .status("PENDING")
                .createdTime(LocalDateTime.now())
                .build();

        DispatchOrder saved = dispatchRepository.save(order);

        // Check operator availability before proceeding
        List<Operator> availableOperators = operatorAvailabilityService.getAvailableOperators();
        List<String> availableOperatorIds = availableOperators.stream()
                .map(Operator::getId)
                .collect(java.util.stream.Collectors.toList());
        if (availableOperatorIds.isEmpty()) {
            log.warn("No available operators for work order: {}", workOrderId);
            DispatchOrder failed = DispatchOrder.builder()
                    .orderId(saved.getOrderId())
                    .workOrderId(saved.getWorkOrderId())
                    .priority(saved.getPriority())
                    .targetRegionCode(saved.getTargetRegionCode())
                    .assigneeId(saved.getAssigneeId())
                    .skillRequired(saved.getSkillRequired())
                    .estimatedDuration(saved.getEstimatedDuration())
                    .status("FAILED")
                    .createdTime(saved.getCreatedTime())
                    .assignedTime(saved.getAssignedTime())
                    .completedTime(saved.getCompletedTime())
                    .build();
            dispatchRepository.save(failed);
            recordHistory(failed, "FAILED", null);
            return dispatchMapper.toResponse(failed);
        }

        // Use scoreAndSelect to pick the best operator
        String bestCandidate = scoreAndSelect(saved, availableOperatorIds);
        if (bestCandidate != null) {
            log.info("Selected best candidate {} for work order {}", bestCandidate, workOrderId);
        }

        return executeDispatch(saved);
    }

    public List<DispatchResponse> batchDispatch(List<String> workOrderIds) {
        log.info("Batch dispatching {} work orders", workOrderIds.size());
        List<DispatchResponse> responses = new ArrayList<>();
        for (String workOrderId : workOrderIds) {
            try {
                DispatchResponse response = dispatch(workOrderId);
                responses.add(response);
                log.debug("Successfully dispatched work order: {}", workOrderId);
            } catch (Exception e) {
                log.error("Failed to dispatch work order: {}, error: {}", workOrderId, e.getMessage());
                DispatchResponse errorResponse = new DispatchResponse();
                errorResponse.setOrderId(UUID.randomUUID().toString());
                errorResponse.setWorkOrderId(workOrderId);
                errorResponse.setStatus("FAILED");
                responses.add(errorResponse);
            }
        }
        log.info("Batch dispatch completed: {}/{} succeeded", responses.size(), workOrderIds.size());
        return responses;
    }

    public DispatchResponse cancelDispatch(String orderId) {
        DispatchOrder existing = dispatchRepository.findById(orderId);
        String status = existing.getStatus();
        if ("COMPLETED".equals(status) || "CANCELLED".equals(status)) {
            throw new DomainException("INVALID_STATE", "Cannot cancel order " + orderId
                    + " in status: " + status);
        }

        DispatchOrder cancelled = DispatchOrder.builder()
                .orderId(existing.getOrderId())
                .workOrderId(existing.getWorkOrderId())
                .priority(existing.getPriority())
                .targetRegionCode(existing.getTargetRegionCode())
                .assigneeId(existing.getAssigneeId())
                .skillRequired(existing.getSkillRequired())
                .estimatedDuration(existing.getEstimatedDuration())
                .status("CANCELLED")
                .createdTime(existing.getCreatedTime())
                .assignedTime(existing.getAssignedTime())
                .completedTime(existing.getCompletedTime())
                .build();

        DispatchOrder saved = dispatchRepository.save(cancelled);
        dispatchEventPublisher.publishCancelled(saved);
        return dispatchMapper.toResponse(saved);
    }

    public DispatchResponse reassign(String orderId, String newAssigneeId) {
        DispatchOrder existing = dispatchRepository.findById(orderId);
        String prevAssignee = existing.getAssigneeId();

        DispatchOrder reassigned = DispatchOrder.builder()
                .orderId(existing.getOrderId())
                .workOrderId(existing.getWorkOrderId())
                .priority(existing.getPriority())
                .targetRegionCode(existing.getTargetRegionCode())
                .assigneeId(newAssigneeId)
                .skillRequired(existing.getSkillRequired())
                .estimatedDuration(existing.getEstimatedDuration())
                .status("ASSIGNED")
                .createdTime(existing.getCreatedTime())
                .assignedTime(LocalDateTime.now())
                .completedTime(existing.getCompletedTime())
                .build();

        DispatchOrder saved = dispatchRepository.save(reassigned);
        dispatchEventPublisher.publishReassigned(saved, prevAssignee);
        recordHistory(saved, "REASSIGNED", prevAssignee);
        return dispatchMapper.toResponse(saved);
    }

    public String getDispatchStatus(String orderId) {
        return dispatchRepository.findById(orderId).getStatus();
    }

    private DispatchResponse executeDispatch(DispatchOrder order) {
        log.info("Executing dispatch for order: {}, workOrder: {}, status: {}",
                order.getOrderId(), order.getWorkOrderId(), order.getStatus());

        DispatchOrder processed = dispatchEngine.dispatch(order);
        log.debug("Engine processed order: {} -> assignee: {}, status: {}",
                processed.getOrderId(), processed.getAssigneeId(), processed.getStatus());

        DispatchOrder saved = dispatchRepository.save(processed);
        recordHistory(saved, "ASSIGNED", null);
        dispatchEventPublisher.publishAssigned(saved);

        DispatchResponse response = dispatchMapper.toResponse(saved);
        log.info("Dispatch completed for order: {}, assignee: {}", saved.getOrderId(), saved.getAssigneeId());
        return response;
    }

    private void recordHistory(DispatchOrder order, String action, String prevAssignee) {
        DispatchHistory history = new DispatchHistory(
                null,
                order.getOrderId(),
                order.getWorkOrderId(),
                order.getAssigneeId(),
                prevAssignee,
                action,
                LocalDateTime.now(),
                action + " for order " + order.getOrderId()
        );
        dispatchHistoryRepository.save(history);
    }

    private String scoreAndSelect(DispatchOrder order, List<String> candidates) {
        String bestCandidate = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (String candidate : candidates) {
            double score = dispatchScoringService.calculateScore(candidate, order);
            if (score > bestScore) {
                bestScore = score;
                bestCandidate = candidate;
            }
        }
        return bestCandidate;
    }

    public DispatchResponse retryDispatch(String orderId) {
        DispatchOrder existing = dispatchRepository.findById(orderId);
        if (!"FAILED".equals(existing.getStatus())) {
            throw new DomainException("INVALID_STATE",
                    "Cannot retry order " + orderId + " in status: " + existing.getStatus());
        }

        DispatchOrder retryOrder = DispatchOrder.builder()
                .orderId(UUID.randomUUID().toString())
                .workOrderId(existing.getWorkOrderId())
                .priority(existing.getPriority())
                .targetRegionCode(existing.getTargetRegionCode())
                .assigneeId(existing.getAssigneeId())
                .skillRequired(existing.getSkillRequired())
                .estimatedDuration(existing.getEstimatedDuration())
                .status("PENDING")
                .createdTime(LocalDateTime.now())
                .build();

        return executeDispatch(retryOrder);
    }

    public List<DispatchHistory> getDispatchHistory(String orderId) {
        List<DispatchHistory> history = dispatchHistoryRepository.findByOrderId(orderId);
        return history != null ? history : Collections.emptyList();
    }

    public List<DispatchResponse> getPendingDispatches() {
        List<DispatchOrder> pendingOrders = dispatchRepository.findByStatus("PENDING");
        if (pendingOrders == null) {
            return Collections.emptyList();
        }
        return pendingOrders.stream()
                .map(dispatchMapper::toResponse)
                .collect(Collectors.toList());
    }

    public DispatchResponse completeDispatch(String orderId) {
        DispatchOrder existing = dispatchRepository.findById(orderId);

        DispatchOrder completed = DispatchOrder.builder()
                .orderId(existing.getOrderId())
                .workOrderId(existing.getWorkOrderId())
                .priority(existing.getPriority())
                .targetRegionCode(existing.getTargetRegionCode())
                .assigneeId(existing.getAssigneeId())
                .skillRequired(existing.getSkillRequired())
                .estimatedDuration(existing.getEstimatedDuration())
                .status("COMPLETED")
                .createdTime(existing.getCreatedTime())
                .assignedTime(existing.getAssignedTime())
                .completedTime(LocalDateTime.now())
                .build();

        DispatchOrder saved = dispatchRepository.save(completed);
        dispatchEventPublisher.publishCompleted(saved);
        return dispatchMapper.toResponse(saved);
    }

    public List<DispatchResponse> getDispatchesByOperator(String operatorId) {
        List<DispatchOrder> orders = dispatchRepository.findByAssignee(operatorId);
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream()
                .map(dispatchMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<DispatchResponse> getDispatchesByPriority(DispatchPriority priority) {
        List<DispatchOrder> orders = dispatchRepository.findByPriority(priority);
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream()
                .map(dispatchMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<DispatchResponse> getDispatchesByRegion(String regionCode) {
        List<DispatchOrder> orders = dispatchRepository.findByRegion(regionCode);
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream()
                .map(dispatchMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDispatcherStatistics() {
        log.info("Calculating dispatcher statistics");
        Map<String, Object> stats = new HashMap<>();

        List<DispatchOrder> allOrders = dispatchRepository.findAll();
        long totalOrders = allOrders.size();
        long pendingCount = allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long assignedCount = allOrders.stream().filter(o -> "ASSIGNED".equals(o.getStatus())).count();
        long completedCount = allOrders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();
        long failedCount = allOrders.stream().filter(o -> "FAILED".equals(o.getStatus())).count();
        long cancelledCount = allOrders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();

        stats.put("totalOrders", totalOrders);
        stats.put("pendingCount", pendingCount);
        stats.put("assignedCount", assignedCount);
        stats.put("completedCount", completedCount);
        stats.put("failedCount", failedCount);
        stats.put("cancelledCount", cancelledCount);
        stats.put("historyCount", dispatchHistoryRepository.count());

        log.info("Dispatcher statistics: total={}, pending={}, assigned={}, completed={}, failed={}, cancelled={}",
                totalOrders, pendingCount, assignedCount, completedCount, failedCount, cancelledCount);

        return stats;
    }

    public DispatchResponse getOrder(String orderId) {
        DispatchOrder order = dispatchRepository.findById(orderId);
        return dispatchMapper.toResponse(order);
    }

    public PagedResult<DispatchResponse> listOrders(String status, String priority, String regionCode, int page, int size) {
        List<DispatchOrder> allOrders = dispatchRepository.findAll();

        if (status != null && !status.isEmpty()) {
            allOrders = allOrders.stream().filter(o -> status.equals(o.getStatus())).collect(Collectors.toList());
        }
        if (regionCode != null && !regionCode.isEmpty()) {
            allOrders = allOrders.stream().filter(o -> regionCode.equals(o.getTargetRegionCode())).collect(Collectors.toList());
        }

        int total = allOrders.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<DispatchOrder> pageOrders;
        if (fromIndex >= total) {
            pageOrders = Collections.emptyList();
        } else {
            pageOrders = allOrders.subList(fromIndex, toIndex);
        }

        List<DispatchResponse> responses = pageOrders.stream()
                .map(dispatchMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResult<>(responses, page, size, total);
    }
}
