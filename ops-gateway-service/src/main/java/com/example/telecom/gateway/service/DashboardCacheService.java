package com.example.telecom.gateway.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class DashboardCacheService {

    private final ConcurrentMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> expiryMap = new ConcurrentHashMap<>();

    private long hits = 0;
    private long misses = 0;

    public Optional<Object> get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            misses++;
            return Optional.empty();
        }
        Long expiry = expiryMap.get(key);
        if (expiry != null && System.currentTimeMillis() > expiry) {
            cache.remove(key);
            expiryMap.remove(key);
            misses++;
            return Optional.empty();
        }
        hits++;
        return Optional.of(entry.getValue());
    }

    public void put(String key, Object value, Duration ttl) {
        cache.put(key, new CacheEntry(key, value));
        if (ttl != null) {
            expiryMap.put(key, System.currentTimeMillis() + ttl.toMillis());
        }
    }

    public void invalidate(String key) {
        cache.remove(key);
        expiryMap.remove(key);
    }

    public void invalidateByPrefix(String prefix) {
        List<String> keysToRemove = cache.keySet().stream()
                .filter(k -> k.startsWith(prefix))
                .collect(Collectors.toList());
        keysToRemove.forEach(key -> {
            cache.remove(key);
            expiryMap.remove(key);
        });
    }

    public void clear() {
        cache.clear();
        expiryMap.clear();
        hits = 0;
        misses = 0;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("size", cache.size());
        stats.put("hits", hits);
        stats.put("misses", misses);
        stats.put("hitRate", (hits + misses) > 0 ? (double) hits / (hits + misses) : 0.0);
        stats.put("keys", new ArrayList<>(cache.keySet()));
        return stats;
    }

    private static class CacheEntry {
        private final String key;
        private final Object value;
        private final long createdTime;

        CacheEntry(String key, Object value) {
            this.key = key;
            this.value = value;
            this.createdTime = System.currentTimeMillis();
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public long getCreatedTime() {
            return createdTime;
        }
    }
}
