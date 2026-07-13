package com.example.telecom.workorder.service;

import com.example.telecom.workorder.domain.WorkOrderTemplate;
import com.example.telecom.workorder.dto.WorkOrderTemplateRequest;
import com.example.telecom.workorder.dto.WorkOrderTemplateResponse;
import com.example.telecom.workorder.repository.WorkOrderTemplateRepository;

import java.util.*;
import java.util.stream.Collectors;

public class WorkOrderTemplateService {

    private final WorkOrderTemplateRepository templateRepository;

    public WorkOrderTemplateService(WorkOrderTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public WorkOrderTemplateResponse createTemplate(WorkOrderTemplateRequest request) {
        String templateId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        WorkOrderTemplate template = new WorkOrderTemplate(
                templateId,
                request.getName(),
                request.getDescription(),
                request.getCategoryId(),
                request.getPriority(),
                request.getDefaultTitle(),
                request.getDefaultDescription(),
                request.getEstimatedDurationMinutes(),
                request.getRequiredSkills() != null ? request.getRequiredSkills() : new ArrayList<>(),
                request.isActive(),
                now,
                now
        );

        templateRepository.save(template);
        return mapToResponse(template);
    }

    public Optional<WorkOrderTemplateResponse> getTemplate(String templateId) {
        return templateRepository.findById(templateId)
                .map(this::mapToResponse);
    }

    public List<WorkOrderTemplateResponse> listTemplates() {
        return templateRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WorkOrderTemplateResponse updateTemplate(String templateId, WorkOrderTemplateRequest request) {
        WorkOrderTemplate existing = templateRepository.findById(templateId)
                .orElseThrow(() -> new NoSuchElementException("Template not found: " + templateId));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategoryId(request.getCategoryId());
        existing.setPriority(request.getPriority());
        existing.setDefaultTitle(request.getDefaultTitle());
        existing.setDefaultDescription(request.getDefaultDescription());
        existing.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        existing.setRequiredSkills(request.getRequiredSkills() != null ? request.getRequiredSkills() : new ArrayList<>());
        existing.setActive(request.isActive());
        existing.setUpdatedTime(System.currentTimeMillis());

        templateRepository.save(existing);
        return mapToResponse(existing);
    }

    public boolean deleteTemplate(String templateId) {
        return templateRepository.deleteById(templateId);
    }

    public List<WorkOrderTemplateResponse> getActiveTemplates() {
        return templateRepository.findAll().stream()
                .filter(WorkOrderTemplate::isActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WorkOrderTemplate applyTemplate(String templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new NoSuchElementException("Template not found: " + templateId));
    }

    public List<WorkOrderTemplateResponse> getTemplatesByCategory(String categoryId) {
        return templateRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public long countTemplates() {
        return templateRepository.count();
    }

    public WorkOrderTemplateResponse activateTemplate(String templateId) {
        WorkOrderTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new NoSuchElementException("Template not found: " + templateId));
        template.setActive(true);
        template.setUpdatedTime(System.currentTimeMillis());
        templateRepository.save(template);
        return mapToResponse(template);
    }

    public WorkOrderTemplateResponse deactivateTemplate(String templateId) {
        WorkOrderTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new NoSuchElementException("Template not found: " + templateId));
        template.setActive(false);
        template.setUpdatedTime(System.currentTimeMillis());
        templateRepository.save(template);
        return mapToResponse(template);
    }

    public WorkOrderTemplateResponse duplicateTemplate(String templateId) {
        WorkOrderTemplate original = templateRepository.findById(templateId)
                .orElseThrow(() -> new NoSuchElementException("Template not found: " + templateId));

        String newId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        WorkOrderTemplate duplicate = new WorkOrderTemplate(
                newId,
                original.getName() + " (Copy)",
                original.getDescription(),
                original.getCategoryId(),
                original.getPriority(),
                original.getDefaultTitle(),
                original.getDefaultDescription(),
                original.getEstimatedDurationMinutes(),
                original.getRequiredSkills() != null
                        ? new ArrayList<>(original.getRequiredSkills()) : new ArrayList<>(),
                false,
                now,
                now
        );

        templateRepository.save(duplicate);
        return mapToResponse(duplicate);
    }

    public Map<String, Object> getTemplateStats() {
        List<WorkOrderTemplate> all = templateRepository.findAll();
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", (long) all.size());
        stats.put("active", all.stream().filter(WorkOrderTemplate::isActive).count());
        stats.put("inactive", all.stream().filter(t -> !t.isActive()).count());
        stats.put("categories", all.stream()
                .map(WorkOrderTemplate::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .count());
        stats.put("withSkills", all.stream()
                .filter(t -> t.getRequiredSkills() != null && !t.getRequiredSkills().isEmpty())
                .count());
        return stats;
    }

    public boolean existsByName(String name) {
        return templateRepository.existsByName(name);
    }

    private WorkOrderTemplateResponse mapToResponse(WorkOrderTemplate template) {
        WorkOrderTemplateResponse response = new WorkOrderTemplateResponse();
        response.setTemplateId(template.getTemplateId());
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setCategoryId(template.getCategoryId());
        response.setPriority(template.getPriority());
        response.setDefaultTitle(template.getDefaultTitle());
        response.setDefaultDescription(template.getDefaultDescription());
        response.setEstimatedDurationMinutes(template.getEstimatedDurationMinutes());
        response.setRequiredSkills(template.getRequiredSkills() != null
                ? new ArrayList<>(template.getRequiredSkills()) : new ArrayList<>());
        response.setActive(template.isActive());
        response.setCreatedTime(template.getCreatedTime());
        response.setUpdatedTime(template.getUpdatedTime());
        return response;
    }
}
