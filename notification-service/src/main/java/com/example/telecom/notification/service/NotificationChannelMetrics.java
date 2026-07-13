package com.example.telecom.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Per-channel metrics counters tracking sent/failed counts, latencies, and providing
 * derived metrics such as failure rate and average latency.
 */
public class NotificationChannelMetrics {

    private final Map<String, AtomicLong> sentCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> failedCounts = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> latencies = new ConcurrentHashMap<>();

    /**
     * Increments the sent count for the given channel.
     *
     * @param channel the channel name
     */
    public void incrementSent(String channel) {
        sentCounts.computeIfAbsent(channel, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * Increments the failed count for the given channel.
     *
     * @param channel the channel name
     */
    public void incrementFailed(String channel) {
        failedCounts.computeIfAbsent(channel, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * Returns the sent count for the given channel.
     *
     * @param channel the channel name
     * @return the number of successful sends
     */
    public long getSentCount(String channel) {
        AtomicLong count = sentCounts.get(channel);
        return count != null ? count.get() : 0L;
    }

    /**
     * Returns the failed count for the given channel.
     *
     * @param channel the channel name
     * @return the number of failed sends
     */
    public long getFailedCount(String channel) {
        AtomicLong count = failedCounts.get(channel);
        return count != null ? count.get() : 0L;
    }

    /**
     * Returns the failure rate for the given channel.
     *
     * @param channel the channel name
     * @return failed / (sent + failed), or 0.0 if no attempts recorded
     */
    public double getFailureRate(String channel) {
        long sent = getSentCount(channel);
        long failed = getFailedCount(channel);
        long total = sent + failed;
        return total > 0 ? (double) failed / total : 0.0;
    }

    /**
     * Returns the average latency for the given channel.
     *
     * @param channel the channel name
     * @return average of stored latencies, or 0.0 if none recorded
     */
    public double getAverageLatency(String channel) {
        List<Long> channelLatencies = latencies.get(channel);
        if (channelLatencies == null || channelLatencies.isEmpty()) {
            return 0.0;
        }
        long sum = 0;
        for (long lat : channelLatencies) {
            sum += lat;
        }
        return (double) sum / channelLatencies.size();
    }

    /**
     * Records a latency measurement for the given channel.
     *
     * @param channel   the channel name
     * @param latencyMs the latency in milliseconds
     */
    public void recordLatency(String channel, long latencyMs) {
        latencies.computeIfAbsent(channel, k -> new ArrayList<>()).add(latencyMs);
    }

    /**
     * Returns a snapshot of all metrics for the given channel.
     *
     * @param channel the channel name
     * @return map containing sentCount, failedCount, failureRate, and averageLatency
     */
    public Map<String, Object> getSnapshot(String channel) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("sentCount", getSentCount(channel));
        snapshot.put("failedCount", getFailedCount(channel));
        snapshot.put("failureRate", getFailureRate(channel));
        snapshot.put("averageLatency", getAverageLatency(channel));
        return snapshot;
    }
}
