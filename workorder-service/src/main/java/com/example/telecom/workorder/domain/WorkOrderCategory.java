package com.example.telecom.workorder.domain;

public class WorkOrderCategory {

    private String categoryId;
    private String name;
    private String description;
    private String parentCategoryId;
    private long slaResponseTimeMs;
    private long slaResolutionTimeMs;
    private long createdTime;
    private long updatedTime;

    public WorkOrderCategory() {
    }

    public WorkOrderCategory(String categoryId, String name, String description,
                             String parentCategoryId, long slaResponseTimeMs,
                             long slaResolutionTimeMs, long createdTime, long updatedTime) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.parentCategoryId = parentCategoryId;
        this.slaResponseTimeMs = slaResponseTimeMs;
        this.slaResolutionTimeMs = slaResolutionTimeMs;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public long getSlaResponseTimeMs() {
        return slaResponseTimeMs;
    }

    public void setSlaResponseTimeMs(long slaResponseTimeMs) {
        this.slaResponseTimeMs = slaResponseTimeMs;
    }

    public long getSlaResolutionTimeMs() {
        return slaResolutionTimeMs;
    }

    public void setSlaResolutionTimeMs(long slaResolutionTimeMs) {
        this.slaResolutionTimeMs = slaResolutionTimeMs;
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
