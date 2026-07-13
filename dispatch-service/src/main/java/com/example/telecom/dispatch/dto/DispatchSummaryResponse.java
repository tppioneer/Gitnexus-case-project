package com.example.telecom.dispatch.dto;

import com.example.telecom.common.dispatch.DispatchPriority;

import java.util.Map;

public class DispatchSummaryResponse {

    private final long totalDispatched;
    private final long pendingCount;
    private final long completedCount;
    private final long cancelledCount;
    private final double averageScore;
    private final Map<DispatchPriority, Long> byPriority;

    public DispatchSummaryResponse(long totalDispatched, long pendingCount,
                                   long completedCount, long cancelledCount,
                                   double averageScore,
                                   Map<DispatchPriority, Long> byPriority) {
        this.totalDispatched = totalDispatched;
        this.pendingCount = pendingCount;
        this.completedCount = completedCount;
        this.cancelledCount = cancelledCount;
        this.averageScore = averageScore;
        this.byPriority = byPriority;
    }

    public long getTotalDispatched() {
        return totalDispatched;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public long getCancelledCount() {
        return cancelledCount;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public Map<DispatchPriority, Long> getByPriority() {
        return byPriority;
    }
}
