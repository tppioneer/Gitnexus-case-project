package com.example.telecom.alarm.rule;

import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceInfo;
import com.example.telecom.common.device.DeviceMetric;

/**
 * Evaluates CPU usage metrics against threshold rules.
 * Contains an overloaded evaluate method for Case B signature disambiguation testing.
 */
public class CpuUsageRuleEvaluator implements RuleEvaluator {

    @Override
    public EvaluationResult evaluate(DeviceMetric metric, ThresholdRule rule) {
        boolean triggered = metric.getValue() > rule.getThresholdValue();
        return new EvaluationResult(
                triggered,
                rule.getRuleId(),
                metric.getMetricId(),
                metric.getValue(),
                rule.getThresholdValue(),
                triggered ? rule.getSeverity() : null,
                triggered ? "CPU usage " + metric.getValue() + "% exceeds threshold " + rule.getThresholdValue() + "%"
                        : "CPU usage normal"
        );
    }

    /**
     * Overloaded evaluate method — NOT part of RuleEvaluator interface.
     * This is a SIGNATURE DISAMBIGUATION noise for Case B.
     */
    public boolean evaluate(DeviceInfo deviceInfo) {
        return deviceInfo != null && deviceInfo.isActive();
    }
}
