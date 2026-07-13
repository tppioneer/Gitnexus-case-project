package com.example.telecom.workorder.dto;

public class WorkOrderCategoryResponse {

    private String categoryId;
    private String name;
    private String description;
    private String parentCategoryId;
    private long slaResponseTimeMs;
    private long slaResolutionTimeMs;
    private long createdTime;
    private long updatedTime;

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
