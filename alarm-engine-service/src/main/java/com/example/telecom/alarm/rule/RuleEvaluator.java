package com.example.telecom.alarm.rule;

import com.example.telecom.common.alarm.EvaluationResult;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.DeviceMetric;

/**
 * Rule evaluator interface for alarm threshold evaluation.
 * Case B target: add EvaluationContext as a third parameter.
 */
public interface RuleEvaluator {
    EvaluationResult evaluate(DeviceMetric metric, ThresholdRule rule);
}
