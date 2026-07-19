package com.example.telecom.change.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the change service. Bound from
 * {@code telecom.change.*} in application properties.
 */
@Component
@ConfigurationProperties(prefix = "telecom.change")
public class ChangeProperties {
    private boolean remoteEnabled = false;
    private int maxRetryCount = 3;
    private String defaultRegion = "default";
    private String pluginClassName = "com.example.telecom.change.plugin.BenchmarkChangeValidationPlugin";

    public boolean isRemoteEnabled() { return remoteEnabled; }
    public void setRemoteEnabled(boolean remoteEnabled) { this.remoteEnabled = remoteEnabled; }
    public int getMaxRetryCount() { return maxRetryCount; }
    public void setMaxRetryCount(int maxRetryCount) { this.maxRetryCount = maxRetryCount; }
    public String getDefaultRegion() { return defaultRegion; }
    public void setDefaultRegion(String defaultRegion) { this.defaultRegion = defaultRegion; }
    public String getPluginClassName() { return pluginClassName; }
    public void setPluginClassName(String pluginClassName) { this.pluginClassName = pluginClassName; }
}
