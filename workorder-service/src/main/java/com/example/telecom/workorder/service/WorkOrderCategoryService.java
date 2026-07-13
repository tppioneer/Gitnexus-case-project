package com.example.telecom.workorder.service;

import com.example.telecom.workorder.domain.WorkOrderCategory;
import com.example.telecom.workorder.dto.WorkOrderCategoryRequest;
import com.example.telecom.workorder.dto.WorkOrderCategoryResponse;
import com.example.telecom.workorder.repository.WorkOrderCategoryRepository;

import java.util.*;
import java.util.stream.Collectors;

public class WorkOrderCategoryService {

    private final WorkOrderCategoryRepository categoryRepository;

    public WorkOrderCategoryService(WorkOrderCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public WorkOrderCategoryResponse createCategory(WorkOrderCategoryRequest request) {
        String categoryId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        WorkOrderCategory category = new WorkOrderCategory(
                categoryId,
                request.getName(),
                request.getDescription(),
                request.getParentCategoryId(),
                request.getSlaResponseTimeMs(),
                request.getSlaResolutionTimeMs(),
                now,
                now
        );

        categoryRepository.save(category);
        return mapToResponse(category);
    }

    public Optional<WorkOrderCategoryResponse> getCategory(String categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::mapToResponse);
    }

    public List<WorkOrderCategoryResponse> listCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WorkOrderCategoryResponse updateCategory(String categoryId, WorkOrderCategoryRequest request) {
        WorkOrderCategory existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found: " + categoryId));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setParentCategoryId(request.getParentCategoryId());
        existing.setSlaResponseTimeMs(request.getSlaResponseTimeMs());
        existing.setSlaResolutionTimeMs(request.getSlaResolutionTimeMs());
        existing.setUpdatedTime(System.currentTimeMillis());

        categoryRepository.save(existing);
        return mapToResponse(existing);
    }

    public boolean deleteCategory(String categoryId) {
        return categoryRepository.deleteById(categoryId);
    }

    public void assignCategory(String workOrderId, String categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new NoSuchElementException("Category not found: " + categoryId);
        }
    }

    public List<WorkOrderCategoryResponse> getRootCategories() {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getParentCategoryId() == null || c.getParentCategoryId().isEmpty())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<WorkOrderCategoryResponse> getChildCategories(String parentCategoryId) {
        return categoryRepository.findByParentCategoryId(parentCategoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<WorkOrderCategoryResponse> findCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(this::mapToResponse);
    }

    public Map<String, Object> getCategoryHierarchy() {
        List<WorkOrderCategory> allCategories = categoryRepository.findAll();
        Map<String, Object> hierarchy = new HashMap<>();
        for (WorkOrderCategory cat : allCategories) {
            if (cat.getParentCategoryId() == null || cat.getParentCategoryId().isEmpty()) {
                hierarchy.put(cat.getCategoryId(), buildSubTree(cat, allCategories));
            }
        }
        return hierarchy;
    }

    private Map<String, Object> buildSubTree(WorkOrderCategory parent, List<WorkOrderCategory> all) {
        Map<String, Object> node = new HashMap<>();
        node.put("name", parent.getName());
        node.put("description", parent.getDescription());
        Map<String, Object> children = new HashMap<>();
        for (WorkOrderCategory cat : all) {
            if (parent.getCategoryId().equals(cat.getParentCategoryId())) {
                children.put(cat.getCategoryId(), buildSubTree(cat, all));
            }
        }
        if (!children.isEmpty()) {
            node.put("children", children);
        }
        return node;
    }

    public long countCategories() {
        return categoryRepository.count();
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public WorkOrderCategoryResponse updateSlaPolicy(String categoryId, long slaResponseTimeMs, long slaResolutionTimeMs) {
        WorkOrderCategory existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found: " + categoryId));

        existing.setSlaResponseTimeMs(slaResponseTimeMs);
        existing.setSlaResolutionTimeMs(slaResolutionTimeMs);
        existing.setUpdatedTime(System.currentTimeMillis());

        categoryRepository.save(existing);
        return mapToResponse(existing);
    }

    private WorkOrderCategoryResponse mapToResponse(WorkOrderCategory category) {
        WorkOrderCategoryResponse response = new WorkOrderCategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setParentCategoryId(category.getParentCategoryId());
        response.setSlaResponseTimeMs(category.getSlaResponseTimeMs());
        response.setSlaResolutionTimeMs(category.getSlaResolutionTimeMs());
        response.setCreatedTime(category.getCreatedTime());
        response.setUpdatedTime(category.getUpdatedTime());
        return response;
    }
}
