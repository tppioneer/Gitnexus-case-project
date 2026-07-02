package com.example.telecom.gateway.dto;

public class WorkOrderSummary {
    private long openWorkOrders;
    private long inProgressWorkOrders;
    private long resolvedToday;
    private long slaBreached;
    private String regionCode;

    public long getOpenWorkOrders() { return openWorkOrders; }
    public void setOpenWorkOrders(long openWorkOrders) { this.openWorkOrders = openWorkOrders; }
    public long getInProgressWorkOrders() { return inProgressWorkOrders; }
    public void setInProgressWorkOrders(long inProgressWorkOrders) { this.inProgressWorkOrders = inProgressWorkOrders; }
    public long getResolvedToday() { return resolvedToday; }
    public void setResolvedToday(long resolvedToday) { this.resolvedToday = resolvedToday; }
    public long getSlaBreached() { return slaBreached; }
    public void setSlaBreached(long slaBreached) { this.slaBreached = slaBreached; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
}
