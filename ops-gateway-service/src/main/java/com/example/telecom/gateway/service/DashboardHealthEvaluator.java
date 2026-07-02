package com.example.telecom.gateway.service;

/**
 * Dashboard health evaluator. Has an evaluate() method but does NOT implement RuleEvaluator.
 * This is a Case B NOISE item — grep will find this evaluate(), but it should not be modified
 * when RuleEvaluator.evaluate() signature changes.
 */
public class DashboardHealthEvaluator {

    public String evaluate(String regionCode) {
        return "Dashboard health for region " + regionCode + ": HEALTHY";
    }

    public int evaluate(int activeDevices, int totalAlarms) {
        if (totalAlarms > activeDevices * 2) return 0; // critical
        if (totalAlarms > activeDevices) return 50;     // warning
        return 100; // healthy
    }
}
