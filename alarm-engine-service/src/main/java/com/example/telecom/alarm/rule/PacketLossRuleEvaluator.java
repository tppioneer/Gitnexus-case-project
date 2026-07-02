package com.example.telecom.alarm.rule;

import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceMetric;

public class PacketLossRuleEvaluator implements RuleEvaluator {

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
                triggered ? "Packet loss " + metric.getValue() + "% exceeds threshold " + rule.getThresholdValue() + "%"
                        : "Packet loss normal"
        );
    }
}
