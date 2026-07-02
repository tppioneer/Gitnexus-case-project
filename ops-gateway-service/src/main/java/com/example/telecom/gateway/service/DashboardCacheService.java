package com.example.telecom.gateway.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DashboardCacheService {

    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();
    private final long defaultTtlMs;

    public DashboardCacheService(long defaultTtlMs) {
        this.defaultTtlMs = defaultTtlMs;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        CacheEntry<?> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        return Optional.of((T) entry.value);
    }

    public <T> void put(String key, T value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + defaultTtlMs));
    }

    public <T> void put(String key, T value, long ttlMs) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMs));
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private static class CacheEntry<T> {
        final T value;
        final long expiresAt;

        CacheEntry(T value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
