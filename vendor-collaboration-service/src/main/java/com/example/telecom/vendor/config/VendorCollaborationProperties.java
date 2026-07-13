package com.example.telecom.vendor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vendor.collaboration")
public class VendorCollaborationProperties {

    private int slaResponseTimeHours = 4;
    private int slaResolutionTimeHours = 48;
    private int autoEscalationMinutes = 120;
    private int maxEscalations = 3;
    private boolean autoEscalationEnabled = true;
    private int digestIntervalHours = 24;
    private int maxTicketsPerVendor = 100;

    public VendorCollaborationProperties() {
    }

    public int getSlaResponseTimeHours() { return slaResponseTimeHours; }
    public void setSlaResponseTimeHours(int slaResponseTimeHours) { this.slaResponseTimeHours = slaResponseTimeHours; }
    public int getSlaResolutionTimeHours() { return slaResolutionTimeHours; }
    public void setSlaResolutionTimeHours(int slaResolutionTimeHours) { this.slaResolutionTimeHours = slaResolutionTimeHours; }
    public int getAutoEscalationMinutes() { return autoEscalationMinutes; }
    public void setAutoEscalationMinutes(int autoEscalationMinutes) { this.autoEscalationMinutes = autoEscalationMinutes; }
    public int getMaxEscalations() { return maxEscalations; }
    public void setMaxEscalations(int maxEscalations) { this.maxEscalations = maxEscalations; }
    public boolean isAutoEscalationEnabled() { return autoEscalationEnabled; }
    public void setAutoEscalationEnabled(boolean autoEscalationEnabled) { this.autoEscalationEnabled = autoEscalationEnabled; }
    public int getDigestIntervalHours() { return digestIntervalHours; }
    public void setDigestIntervalHours(int digestIntervalHours) { this.digestIntervalHours = digestIntervalHours; }
    public int getMaxTicketsPerVendor() { return maxTicketsPerVendor; }
    public void setMaxTicketsPerVendor(int maxTicketsPerVendor) { this.maxTicketsPerVendor = maxTicketsPerVendor; }
}
