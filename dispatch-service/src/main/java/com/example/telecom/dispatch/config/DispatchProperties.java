package com.example.telecom.dispatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dispatch")
public class DispatchProperties {

    private int maxAssignmentsPerOperator = 10;
    private boolean scoringEnabled = true;
    private int defaultRulePriority = 5;
    private double proximityWeight = 0.3;
    private double skillWeight = 0.4;
    private double loadWeight = 0.3;

    public int getMaxAssignmentsPerOperator() {
        return maxAssignmentsPerOperator;
    }

    public void setMaxAssignmentsPerOperator(int maxAssignmentsPerOperator) {
        this.maxAssignmentsPerOperator = maxAssignmentsPerOperator;
    }

    public boolean isScoringEnabled() {
        return scoringEnabled;
    }

    public void setScoringEnabled(boolean scoringEnabled) {
        this.scoringEnabled = scoringEnabled;
    }

    public int getDefaultRulePriority() {
        return defaultRulePriority;
    }

    public void setDefaultRulePriority(int defaultRulePriority) {
        this.defaultRulePriority = defaultRulePriority;
    }

    public double getProximityWeight() {
        return proximityWeight;
    }

    public void setProximityWeight(double proximityWeight) {
        this.proximityWeight = proximityWeight;
    }

    public double getSkillWeight() {
        return skillWeight;
    }

    public void setSkillWeight(double skillWeight) {
        this.skillWeight = skillWeight;
    }

    public double getLoadWeight() {
        return loadWeight;
    }

    public void setLoadWeight(double loadWeight) {
        this.loadWeight = loadWeight;
    }
}
