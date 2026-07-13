package com.example.telecom.dispatch.service;

import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.common.dispatch.DispatchPriority;
import com.example.telecom.dispatch.domain.DispatchHistory;
import com.example.telecom.dispatch.dto.DispatchSummaryResponse;
import com.example.telecom.dispatch.repository.DispatchHistoryRepository;
import com.example.telecom.dispatch.repository.DispatchRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DispatchStatisticsService {

    private final DispatchRepository dispatchRepository;
    private final DispatchHistoryRepository dispatchHistoryRepository;

    private final ConcurrentHashMap<String, Long> operatorDispatchCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> operatorTotalMinutes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> operatorCompletedCounts = new ConcurrentHashMap<>();

    public DispatchStatisticsService(DispatchRepository dispatchRepository,
                                     DispatchHistoryRepository dispatchHistoryRepository) {
        this.dispatchRepository = dispatchRepository;
        this.dispatchHistoryRepository = dispatchHistoryRepository;
    }

    /**
     * Returns overall dispatch statistics including totals, pending/completed/cancelled counts,
     * average score, and breakdown by priority.
     */
    public DispatchSummaryResponse getOverallStatistics() {
        List<DispatchOrder> allOrders = dispatchRepository.findAll();

        long totalDispatched = allOrders.size();
        long pendingCount = countByStatus(allOrders, "PENDING");
        long completedCount = countByStatus(allOrders, "COMPLETED");
        long cancelledCount = countByStatus(allOrders, "CANCELLED");

        double averageScore = computeOverallScore(allOrders);

        Map<DispatchPriority, Long> byPriority = buildPriorityCounts(allOrders);

        return new DispatchSummaryResponse(totalDispatched, pendingCount, completedCount,
                cancelledCount, averageScore, byPriority);
    }

    /**
     * Returns a map of statistics keyed by metric name for a specific operator.
     */
    public Map<String, Object> getOperatorStatistics(String operatorId) {
        if (operatorId == null || operatorId.isBlank()) {
            return Collections.emptyMap();
        }

        List<DispatchOrder> operatorOrders = dispatchRepository.findByAssignee(operatorId);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("operatorId", operatorId);
        stats.put("totalDispatched", (long) operatorOrders.size());

        long completed = countByStatus(operatorOrders, "COMPLETED");
        long pending = countByStatus(operatorOrders, "PENDING");
        long cancelled = countByStatus(operatorOrders, "CANCELLED");

        stats.put("completedCount", completed);
        stats.put("pendingCount", pending);
        stats.put("cancelledCount", cancelled);

        double avgMinutes = operatorOrders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus())
                        && o.getCreatedTime() != null
                        && o.getCompletedTime() != null)
                .mapToLong(o -> ChronoUnit.MINUTES.between(o.getCreatedTime(), o.getCompletedTime()))
                .average()
                .orElse(0.0);
        stats.put("averageCompletionMinutes", avgMinutes);

        Map<DispatchPriority, Long> byPriority = buildPriorityCounts(operatorOrders);
        stats.put("byPriority", byPriority);

        List<DispatchHistory> historyEntries = dispatchHistoryRepository.findByOperatorId(operatorId);
        stats.put("historyEntries", (long) historyEntries.size());

        operatorDispatchCounts.put(operatorId, (long) operatorOrders.size());
        operatorCompletedCounts.put(operatorId, completed);

        return stats;
    }

    /**
     * Returns a list of map entries representing dispatch trends over the given date range.
     * Each entry contains date and count information for each day in the range.
     */
    public List<Map<String, Object>> getDispatchTrend(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            return Collections.emptyList();
        }

        List<DispatchOrder> allOrders = dispatchRepository.findAll();

        Map<LocalDate, List<DispatchOrder>> groupedByDate = allOrders.stream()
                .filter(o -> o.getCreatedTime() != null)
                .filter(o -> {
                    LocalDate d = o.getCreatedTime().toLocalDate();
                    return !d.isBefore(from) && !d.isAfter(to);
                })
                .collect(Collectors.groupingBy(o -> o.getCreatedTime().toLocalDate()));

        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            Map<String, Object> entry = new LinkedHashMap<>();
            List<DispatchOrder> dayOrders = groupedByDate.getOrDefault(current, Collections.emptyList());

            entry.put("date", current.toString());
            entry.put("count", (long) dayOrders.size());
            entry.put("completed", countByStatus(dayOrders, "COMPLETED"));
            entry.put("pending", countByStatus(dayOrders, "PENDING"));
            entry.put("cancelled", countByStatus(dayOrders, "CANCELLED"));

            double dayAvgMinutes = dayOrders.stream()
                    .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus())
                            && o.getCreatedTime() != null
                            && o.getCompletedTime() != null)
                    .mapToLong(o -> ChronoUnit.MINUTES.between(o.getCreatedTime(), o.getCompletedTime()))
                    .average()
                    .orElse(0.0);
            entry.put("averageCompletionMinutes", dayAvgMinutes);

            trend.add(entry);
            current = current.plusDays(1);
        }

        return trend;
    }

    /**
     * Returns the average dispatch time (in minutes) across all completed orders,
     * computed from creation time to completion time.
     */
    public double getAverageDispatchTime() {
        List<DispatchOrder> completedOrders = dispatchRepository.findAll().stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus())
                        && o.getCreatedTime() != null
                        && o.getCompletedTime() != null)
                .collect(Collectors.toList());

        if (completedOrders.isEmpty()) {
            return 0.0;
        }

        long totalMinutes = completedOrders.stream()
                .mapToLong(o -> ChronoUnit.MINUTES.between(o.getCreatedTime(), o.getCompletedTime()))
                .sum();

        return (double) totalMinutes / completedOrders.size();
    }

    /**
     * Returns a map of dispatch counts grouped by priority level.
     * Includes all priorities with zero counts for completeness.
     */
    public Map<DispatchPriority, Long> getDispatchCountByPriority() {
        return buildPriorityCounts(dispatchRepository.findAll());
    }

    /**
     * Records a completed dispatch time for local operator tracking.
     */
    public void recordOperatorDispatchTime(String operatorId, long minutes) {
        operatorTotalMinutes.merge(operatorId, minutes, Long::sum);
        operatorDispatchCounts.merge(operatorId, 1L, Long::sum);
    }

    /**
     * Returns the locally tracked average completion time for a given operator, if available.
     */
    public Optional<Double> getLocalOperatorAverageTime(String operatorId) {
        Long count = operatorDispatchCounts.get(operatorId);
        Long totalMinutes = operatorTotalMinutes.get(operatorId);
        if (count == null || count == 0 || totalMinutes == null) {
            return Optional.empty();
        }
        return Optional.of(totalMinutes.doubleValue() / count);
    }

    /**
     * Clears all locally tracked statistics.
     */
    public void resetLocalStatistics() {
        operatorDispatchCounts.clear();
        operatorTotalMinutes.clear();
        operatorCompletedCounts.clear();
    }

    // -------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------

    private long countByStatus(List<DispatchOrder> orders, String status) {
        return orders.stream()
                .filter(o -> status.equalsIgnoreCase(o.getStatus()))
                .count();
    }

    private Map<DispatchPriority, Long> buildPriorityCounts(List<DispatchOrder> orders) {
        Map<DispatchPriority, Long> counts = new EnumMap<>(DispatchPriority.class);
        for (DispatchPriority priority : DispatchPriority.values()) {
            counts.put(priority, 0L);
        }
        orders.stream()
                .filter(o -> o.getPriority() != null)
                .collect(Collectors.groupingBy(DispatchOrder::getPriority, Collectors.counting()))
                .forEach(counts::put);
        return counts;
    }

    private double computeOverallScore(List<DispatchOrder> orders) {
        List<DispatchOrder> completed = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus())
                        && o.getCreatedTime() != null
                        && o.getCompletedTime() != null)
                .collect(Collectors.toList());

        if (completed.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        for (DispatchOrder o : completed) {
            long minutes = ChronoUnit.MINUTES.between(o.getCreatedTime(), o.getCompletedTime());
            int estimated = o.getEstimatedDuration();
            if (estimated <= 0) {
                totalScore += 50.0;
            } else if (minutes <= estimated) {
                totalScore += 100.0;
            } else if (minutes <= estimated * 1.5) {
                totalScore += 75.0;
            } else if (minutes <= estimated * 2.0) {
                totalScore += 50.0;
            } else {
                totalScore += 25.0;
            }
        }

        return totalScore / completed.size();
    }
}
