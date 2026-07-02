package com.example.telecom.notification.config;

public class NotificationProperties {
    private int maxRetries = 3;
    private long retryDelayMs = 5000;
    private boolean smsEnabled = true;
    private boolean emailEnabled = true;
    private boolean wecomEnabled = true;

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    public long getRetryDelayMs() { return retryDelayMs; }
    public void setRetryDelayMs(long retryDelayMs) { this.retryDelayMs = retryDelayMs; }
    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public boolean isWecomEnabled() { return wecomEnabled; }
    public void setWecomEnabled(boolean wecomEnabled) { this.wecomEnabled = wecomEnabled; }
}
