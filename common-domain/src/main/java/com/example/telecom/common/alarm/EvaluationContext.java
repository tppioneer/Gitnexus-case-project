package com.example.telecom.common.alarm;

import java.util.HashMap;
import java.util.Map;

/**
 * Evaluation context for rule evaluation.
 * Case B target: add this as a parameter to RuleEvaluator.evaluate().
 */
public class EvaluationContext {
    private String deviceId;
    private String deviceRegionCode;
    private Map<String, Object> attributes;

    public EvaluationContext() {
        this.attributes = new HashMap<>();
    }

    public EvaluationContext(String deviceId, String deviceRegionCode) {
        this.deviceId = deviceId;
        this.deviceRegionCode = deviceRegionCode;
        this.attributes = new HashMap<>();
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getDeviceRegionCode() { return deviceRegionCode; }
    public void setDeviceRegionCode(String deviceRegionCode) { this.deviceRegionCode = deviceRegionCode; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }

    public void putAttribute(String key, Object value) { this.attributes.put(key, value); }
    public Object getAttribute(String key) { return this.attributes.get(key); }
}
