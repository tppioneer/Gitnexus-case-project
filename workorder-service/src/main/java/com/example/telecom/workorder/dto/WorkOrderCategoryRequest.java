package com.example.telecom.workorder.dto;

public class WorkOrderCategoryRequest {

    private String name;
    private String description;
    private String parentCategoryId;
    private long slaResponseTimeMs;
    private long slaResolutionTimeMs;

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
}
