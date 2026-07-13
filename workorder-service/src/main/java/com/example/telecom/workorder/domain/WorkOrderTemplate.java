package com.example.telecom.workorder.domain;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderTemplate {

    private String templateId;
    private String name;
    private String description;
    private String categoryId;
    private String priority;
    private String defaultTitle;
    private String defaultDescription;
    private int estimatedDurationMinutes;
    private List<String> requiredSkills;
    private boolean isActive;
    private long createdTime;
    private long updatedTime;

    public WorkOrderTemplate() {
        this.requiredSkills = new ArrayList<>();
    }

    public WorkOrderTemplate(String templateId, String name, String description,
                             String categoryId, String priority, String defaultTitle,
                             String defaultDescription, int estimatedDurationMinutes,
                             List<String> requiredSkills, boolean isActive,
                             long createdTime, long updatedTime) {
        this.templateId = templateId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.priority = priority;
        this.defaultTitle = defaultTitle;
        this.defaultDescription = defaultDescription;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.requiredSkills = requiredSkills;
        this.isActive = isActive;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    public void setDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }
}
