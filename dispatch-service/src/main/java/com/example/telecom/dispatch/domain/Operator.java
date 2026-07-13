package com.example.telecom.dispatch.domain;

public class Operator {

    private final String id;
    private final String name;
    private final String regionCode;
    private final boolean available;
    private final int currentLoad;

    public Operator(String id, String name, String regionCode, boolean available, int currentLoad) {
        this.id = id;
        this.name = name;
        this.regionCode = regionCode;
        this.available = available;
        this.currentLoad = currentLoad;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }
}
