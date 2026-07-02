package com.example.telecom.gateway.config;

public class OpsGatewayProperties {
    private int cacheTtlSeconds = 60;
    private int maxDashboardItems = 100;
    private String defaultRegion = "all";

    public int getCacheTtlSeconds() { return cacheTtlSeconds; }
    public void setCacheTtlSeconds(int cacheTtlSeconds) { this.cacheTtlSeconds = cacheTtlSeconds; }
    public int getMaxDashboardItems() { return maxDashboardItems; }
    public void setMaxDashboardItems(int maxDashboardItems) { this.maxDashboardItems = maxDashboardItems; }
    public String getDefaultRegion() { return defaultRegion; }
    public void setDefaultRegion(String defaultRegion) { this.defaultRegion = defaultRegion; }
}
