package com.example.telecom.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks per-channel statistics including send/failure counts, latency, and success rates.
 */
public class NotificationChannelStatsService {

    private final Map<String, ChannelStats> channelStats = new ConcurrentHashMap<>();

    /**
     * Inner class representing statistics for a single notification channel.
     */
    public static class ChannelStats {
        private final AtomicLong sentCount = new AtomicLong(0);
        private final AtomicLong failedCount = new AtomicLong(0);
        private final AtomicLong totalLatency = new AtomicLong(0);
        private final AtomicLong messageCount = new AtomicLong(0);

        public long getSentCount() {
            return sentCount.get();
        }

        public long getFailedCount() {
            return failedCount.get();
        }

        public long getTotalLatency() {
            return totalLatency.get();
        }

        public long getMessageCount() {
            return messageCount.get();
        }
    }

    /**
     * Records a send attempt for the given channel.
     *
     * @param channel the channel name
     * @param latency the latency in milliseconds
     * @param success whether the send was successful
     */
    public void recordSend(String channel, long latency, boolean success) {
        ChannelStats stats = channelStats.computeIfAbsent(channel, k -> new ChannelStats());
        if (success) {
            stats.sentCount.incrementAndGet();
        } else {
            stats.failedCount.incrementAndGet();
        }
        stats.totalLatency.addAndGet(latency);
        stats.messageCount.incrementAndGet();
    }

    /**
     * Returns a map of statistics for the given channel.
     *
     * @param channel the channel name
     * @return map containing sentCount, failedCount, averageLatency, and successRate
     */
    public Map<String, Object> getChannelStats(String channel) {
        ChannelStats stats = channelStats.get(channel);
        Map<String, Object> result = new HashMap<>();
        if (stats == null) {
            result.put("sentCount", 0L);
            result.put("failedCount", 0L);
            result.put("averageLatency", 0.0);
            result.put("successRate", 0.0);
            return result;
        }

        long sent = stats.sentCount.get();
        long failed = stats.failedCount.get();
        long totalLat = stats.totalLatency.get();
        long msgCount = stats.messageCount.get();

        double averageLatency = msgCount > 0 ? (double) totalLat / msgCount : 0.0;
        double successRate = (sent + failed) > 0 ? (double) sent / (sent + failed) : 0.0;

        result.put("sentCount", sent);
        result.put("failedCount", failed);
        result.put("averageLatency", averageLatency);
        result.put("successRate", successRate);

        return result;
    }

    /**
     * Returns a map of all channel names to their statistics maps.
     *
     * @return map of channel names to stats maps
     */
    public Map<String, Map<String, Object>> getAllChannelStats() {
        Map<String, Map<String, Object>> allStats = new HashMap<>();
        for (String channel : channelStats.keySet()) {
            allStats.put(channel, getChannelStats(channel));
        }
        return allStats;
    }

    /**
     * Returns channels sorted by success rate descending, with ties broken by sentCount descending.
     *
     * @return list of channel stat maps sorted by success rate
     */
    public List<Map<String, Object>> getChannelRanking() {
        List<Map<String, Object>> rankings = new ArrayList<>();
        for (String channel : channelStats.keySet()) {
            Map<String, Object> entry = new HashMap<>(getChannelStats(channel));
            entry.put("channel", channel);
            rankings.add(entry);
        }

        rankings.sort((a, b) -> {
            double rateA = (double) a.get("successRate");
            double rateB = (double) b.get("successRate");
            int cmp = Double.compare(rateB, rateA);
            if (cmp != 0) {
                return cmp;
            }
            long sentA = (long) a.get("sentCount");
            long sentB = (long) b.get("sentCount");
            return Long.compare(sentB, sentA);
        });

        return rankings;
    }
}
