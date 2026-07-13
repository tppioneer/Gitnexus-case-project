package com.example.telecom.dispatch.domain;

public class DispatchRuleResult {

    private final String ruleName;
    private final double score;
    private final boolean passed;
    private final String details;

    public DispatchRuleResult(String ruleName, double score, boolean passed, String details) {
        this.ruleName = ruleName;
        this.score = score;
        this.passed = passed;
        this.details = details;
    }

    public String getRuleName() {
        return ruleName;
    }

    public double getScore() {
        return score;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getDetails() {
        return details;
    }

    public boolean hasPassedWithHighScore() {
        return passed && score >= 1.0;
    }

    public boolean hasFailed() {
        return !passed;
    }

    @Override
    public String toString() {
        return "DispatchRuleResult{ruleName='" + ruleName + "', score=" + score + ", passed=" + passed + "}";
    }
}
