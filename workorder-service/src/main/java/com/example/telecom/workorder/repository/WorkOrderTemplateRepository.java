package com.example.telecom.workorder.repository;

import com.example.telecom.workorder.domain.WorkOrderTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkOrderTemplateRepository {
    private final Map<String, WorkOrderTemplate> templates = new ConcurrentHashMap<>();

    public WorkOrderTemplate save(WorkOrderTemplate template) {
        templates.put(template.getTemplateId(), template);
        return template;
    }

    public Optional<WorkOrderTemplate> findById(String templateId) {
        return Optional.ofNullable(templates.get(templateId));
    }

    public List<WorkOrderTemplate> findAll() {
        return new ArrayList<>(templates.values());
    }

    public boolean deleteById(String templateId) {
        return templates.remove(templateId) != null;
    }

    public long count() {
        return templates.size();
    }

    public List<WorkOrderTemplate> findByCategoryId(String categoryId) {
        return templates.values().stream()
                .filter(t -> categoryId.equals(t.getCategoryId()))
                .toList();
    }

    public boolean existsByName(String name) {
        return templates.values().stream()
                .anyMatch(t -> t.getName().equalsIgnoreCase(name));
    }
}
