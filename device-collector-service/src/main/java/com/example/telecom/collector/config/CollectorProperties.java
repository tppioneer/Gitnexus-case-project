package com.example.telecom.collector.config;

public class CollectorProperties {
    private int metricBatchSize = 100;
    private long ingestTimeoutMs = 5000;
    private boolean validationEnabled = true;

    public int getMetricBatchSize() { return metricBatchSize; }
    public void setMetricBatchSize(int metricBatchSize) { this.metricBatchSize = metricBatchSize; }
    public long getIngestTimeoutMs() { return ingestTimeoutMs; }
    public void setIngestTimeoutMs(long ingestTimeoutMs) { this.ingestTimeoutMs = ingestTimeoutMs; }
    public boolean isValidationEnabled() { return validationEnabled; }
    public void setValidationEnabled(boolean validationEnabled) { this.validationEnabled = validationEnabled; }
}
