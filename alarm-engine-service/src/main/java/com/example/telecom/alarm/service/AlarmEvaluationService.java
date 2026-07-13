package com.example.telecom.alarm.service;

import com.example.telecom.alarm.event.AlarmEventPublisher;
import com.example.telecom.alarm.mapper.AlarmRecordMapper;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.repository.ThresholdRuleRepository;
import com.example.telecom.alarm.rule.RuleEvaluator;
import com.example.telecom.alarm.rule.RuleEvaluatorRegistry;
import com.example.telecom.common.alarm.*;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.device.DeviceMetric;
import com.example.telecom.common.device.MetricType;

import java.util.ArrayList;
import java.util.List;

/**
 * Core alarm evaluation service.
 * MUST call RuleEvaluator.evaluate() through the interface variable, NOT via new.
 * This is the critical Case B test point.
 */
public class AlarmEvaluationService {

    private final ThresholdRuleRepository thresholdRuleRepository;
    private final RuleEvaluatorRegistry ruleEvaluatorRegistry;
    private final AlarmSeverityClassifier alarmSeverityClassifier;
    private final AlarmDeduplicationService alarmDeduplicationService;
    private final AlarmCorrelationService alarmCorrelationService;
    private final AlarmRepository alarmRepository;
    private final AlarmEventPublisher alarmEventPublisher;
    private final AlarmRecordMapper alarmRecordMapper;

    public AlarmEvaluationService(ThresholdRuleRepository thresholdRuleRepository,
                                   RuleEvaluatorRegistry ruleEvaluatorRegistry,
                                   AlarmSeverityClassifier alarmSeverityClassifier,
                                   AlarmDeduplicationService alarmDeduplicationService,
                                   AlarmCorrelationService alarmCorrelationService,
                                   AlarmRepository alarmRepository,
                                   AlarmEventPublisher alarmEventPublisher,
                                   AlarmRecordMapper alarmRecordMapper) {
        this.thresholdRuleRepository = thresholdRuleRepository;
        this.ruleEvaluatorRegistry = ruleEvaluatorRegistry;
        this.alarmSeverityClassifier = alarmSeverityClassifier;
        this.alarmDeduplicationService = alarmDeduplicationService;
        this.alarmCorrelationService = alarmCorrelationService;
        this.alarmRepository = alarmRepository;
        this.alarmEventPublisher = alarmEventPublisher;
        this.alarmRecordMapper = alarmRecordMapper;
    }

    /**
     * Evaluate a device metric event and generate alarms if thresholds are exceeded.
     * Calls RuleEvaluator.evaluate() THROUGH THE INTERFACE VARIABLE (not new).
     */
    public void evaluate(DeviceMetricEvent event) {
        MetricType metricType = MetricType.valueOf(event.getMetricType());
        List<ThresholdRule> rules = thresholdRuleRepository.loadRules(metricType);

        for (ThresholdRule rule : rules) {
            // MUST call through interface variable — Case B constraint
            RuleEvaluator evaluator = ruleEvaluatorRegistry.resolve(rule.getMetricType() != null
                    ? MetricType.valueOf(rule.getMetricType())
                    : metricType);

            // Interface variable call — NOT new CpuUsageRuleEvaluator().evaluate(...)
            EvaluationResult result = evaluator.evaluate(
                    alarmRecordMapper.toDeviceMetric(event), rule);

            if (result.isTriggered()) {
                Severity severity = alarmSeverityClassifier.classify(result, rule);

                AlarmRecord alarm = alarmRecordMapper.toAlarmRecord(event, result, severity);
                alarm.setAlarmRegionCode(event.getDeviceRegionCode());

                if (!alarmDeduplicationService.isDuplicate(alarm)) {
                    alarmRepository.save(alarm);
                    alarmCorrelationService.correlate(alarm);

                    AlarmEvent alarmEvent = alarmRecordMapper.toAlarmEvent(alarm);
                    alarmEventPublisher.publish(alarmEvent);
                }
            }
        }
    }

    /**
     * Evaluates a batch of threshold rules against a single device metric.
     * Returns all evaluation results, including those that did not trigger.
     * This is useful for bulk rule evaluation without generating alarms.
     *
     * @param rules  the list of threshold rules to evaluate
     * @param metric the device metric to evaluate against
     * @return list of evaluation results for all rules
     */
    public List<EvaluationResult> evaluateBatch(List<ThresholdRule> rules, DeviceMetric metric) {
        if (rules == null || rules.isEmpty()) {
            return List.of();
        }
        List<EvaluationResult> results = new ArrayList<>();
        for (ThresholdRule rule : rules) {
            if (!rule.isEnabled()) {
                continue;
            }
            MetricType metricType = metric.getMetricType() != null
                    ? metric.getMetricType()
                    : MetricType.valueOf(rule.getMetricType());
            RuleEvaluator evaluator = ruleEvaluatorRegistry.resolve(metricType);
            EvaluationResult result = evaluator.evaluate(metric, rule);
            results.add(result);
        }
        return results;
    }
}
