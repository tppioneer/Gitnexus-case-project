package com.example.telecom.alarm.config;

public class AlarmEngineProperties {
    private boolean dedupEnabled = true;
    private boolean correlationEnabled = true;
    private int maxAlarmsPerDevice = 100;

    public boolean isDedupEnabled() { return dedupEnabled; }
    public void setDedupEnabled(boolean dedupEnabled) { this.dedupEnabled = dedupEnabled; }
    public boolean isCorrelationEnabled() { return correlationEnabled; }
    public void setCorrelationEnabled(boolean correlationEnabled) { this.correlationEnabled = correlationEnabled; }
    public int getMaxAlarmsPerDevice() { return maxAlarmsPerDevice; }
    public void setMaxAlarmsPerDevice(int maxAlarmsPerDevice) { this.maxAlarmsPerDevice = maxAlarmsPerDevice; }
}
