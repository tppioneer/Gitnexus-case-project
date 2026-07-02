package com.example.telecom.alarm.rule;

import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceMetric;

public class OpticalPowerRuleEvaluator implements RuleEvaluator {

    @Override
    public EvaluationResult evaluate(DeviceMetric metric, ThresholdRule rule) {
        // Optical power: lower value means weaker signal (below threshold = alarm)
        boolean triggered = metric.getValue() < rule.getThresholdValue();
        return new EvaluationResult(
                triggered,
                rule.getRuleId(),
                metric.getMetricId(),
                metric.getValue(),
                rule.getThresholdValue(),
                triggered ? rule.getSeverity() : null,
                triggered ? "Optical power " + metric.getValue() + " dBm below threshold " + rule.getThresholdValue() + " dBm"
                        : "Optical power normal"
        );
    }
}
