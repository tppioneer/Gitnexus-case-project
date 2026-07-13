package com.example.telecom.dispatch.repository;

import com.example.telecom.common.exception.DomainException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DispatchRuleRepository {

    public static class DispatchRuleEntity {
        private String ruleId;
        private String name;
        private String ruleType;
        private int priority;
        private boolean enabled;
        private Map<String, String> configuration;
        private LocalDateTime createdTime;

        public String getRuleId() {
            return ruleId;
        }

        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRuleType() {
            return ruleType;
        }

        public void setRuleType(String ruleType) {
            this.ruleType = ruleType;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, String> getConfiguration() {
            return configuration;
        }

        public void setConfiguration(Map<String, String> configuration) {
            this.configuration = configuration;
        }

        public LocalDateTime getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(LocalDateTime createdTime) {
            this.createdTime = createdTime;
        }
    }

    private final ConcurrentHashMap<String, DispatchRuleEntity> store = new ConcurrentHashMap<>();
    private final List<String> orderedIds = new ArrayList<>();

    public DispatchRuleEntity save(DispatchRuleEntity entity) {
        if (entity.getRuleId() == null) {
            entity.setRuleId(UUID.randomUUID().toString());
        }
        boolean isNew = !store.containsKey(entity.getRuleId());
        store.put(entity.getRuleId(), entity);
        if (isNew) {
            orderedIds.add(entity.getRuleId());
        }
        return entity;
    }

    public DispatchRuleEntity findById(String id) {
        DispatchRuleEntity entity = store.get(id);
        if (entity == null) {
            throw new DomainException("NOT_FOUND", "Rule not found: " + id);
        }
        return entity;
    }

    public List<DispatchRuleEntity> findAll() {
        List<DispatchRuleEntity> result = new ArrayList<>();
        for (String id : orderedIds) {
            DispatchRuleEntity entity = store.get(id);
            if (entity != null) {
                result.add(entity);
            }
        }
        return result;
    }

    public List<DispatchRuleEntity> findByPriority(int priority) {
        return store.values().stream()
                .filter(entity -> entity.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<DispatchRuleEntity> findActiveRules() {
        return store.values().stream()
                .filter(DispatchRuleEntity::isEnabled)
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        store.remove(id);
        orderedIds.remove(id);
    }

    public void reorder(List<String> ids) {
        orderedIds.clear();
        orderedIds.addAll(ids);
    }

    public long count() {
        return store.size();
    }
}
