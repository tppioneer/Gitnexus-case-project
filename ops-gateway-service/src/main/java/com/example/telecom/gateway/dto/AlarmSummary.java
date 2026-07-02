package com.example.telecom.gateway.dto;

public class AlarmSummary {
    private long totalAlarms;
    private long criticalAlarms;
    private long majorAlarms;
    private long warningAlarms;
    private String regionCode;

    public long getTotalAlarms() { return totalAlarms; }
    public void setTotalAlarms(long totalAlarms) { this.totalAlarms = totalAlarms; }
    public long getCriticalAlarms() { return criticalAlarms; }
    public void setCriticalAlarms(long criticalAlarms) { this.criticalAlarms = criticalAlarms; }
    public long getMajorAlarms() { return majorAlarms; }
    public void setMajorAlarms(long majorAlarms) { this.majorAlarms = majorAlarms; }
    public long getWarningAlarms() { return warningAlarms; }
    public void setWarningAlarms(long warningAlarms) { this.warningAlarms = warningAlarms; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
}
