package com.example.telecom.workorder.repository;

import com.example.telecom.workorder.domain.WorkOrderCategory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkOrderCategoryRepository {
    private final Map<String, WorkOrderCategory> categories = new ConcurrentHashMap<>();

    public WorkOrderCategory save(WorkOrderCategory category) {
        categories.put(category.getCategoryId(), category);
        return category;
    }

    public Optional<WorkOrderCategory> findById(String categoryId) {
        return Optional.ofNullable(categories.get(categoryId));
    }

    public List<WorkOrderCategory> findAll() {
        return new ArrayList<>(categories.values());
    }

    public boolean deleteById(String categoryId) {
        return categories.remove(categoryId) != null;
    }

    public long count() {
        return categories.size();
    }

    public boolean existsByName(String name) {
        return categories.values().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    public Optional<WorkOrderCategory> findByName(String name) {
        return categories.values().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<WorkOrderCategory> findByParentCategoryId(String parentCategoryId) {
        return categories.values().stream()
                .filter(c -> parentCategoryId.equals(c.getParentCategoryId()))
                .collect(java.util.stream.Collectors.toList());
    }
}
