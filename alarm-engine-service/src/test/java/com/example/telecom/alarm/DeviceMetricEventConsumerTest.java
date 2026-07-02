package com.example.telecom.alarm;

import com.example.telecom.alarm.consumer.DeviceMetricEventConsumer;
import com.example.telecom.alarm.event.AlarmEventPublisher;
import com.example.telecom.alarm.mapper.AlarmRecordMapper;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.repository.ThresholdRuleRepository;
import com.example.telecom.alarm.rule.*;
import com.example.telecom.alarm.service.*;
import com.example.telecom.common.alarm.*;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.event.DomainEventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMetricEventConsumerTest {

    private DeviceMetricEventConsumer consumer;
    private AlarmRepository alarmRepository;

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepository();
        ThresholdRuleRepository ruleRepo = new ThresholdRuleRepository();
        ruleRepo.save(new ThresholdRule("r1", "CPU", "CPU_USAGE", 50.0, Severity.WARNING, true));

        RuleEvaluatorRegistry registry = new RuleEvaluatorRegistry(List.of(
                new CpuUsageRuleEvaluator(), new MemoryUsageRuleEvaluator(),
                new OpticalPowerRuleEvaluator(), new PacketLossRuleEvaluator(),
                new TemperatureRuleEvaluator()
        ));

        AlarmSeverityClassifier classifier = new AlarmSeverityClassifier();
        AlarmDeduplicationService dedup = new AlarmDeduplicationService(alarmRepository);
        AlarmCorrelationService correlation = new AlarmCorrelationService(alarmRepository);
        AlarmEventPublisher publisher = new AlarmEventPublisher(new DomainEventBus() {
            @Override public void publish(com.example.telecom.common.device.DeviceMetricEvent event) {}
            @Override public void publish(AlarmEvent event) {}
            @Override public void publish(com.example.telecom.common.workorder.WorkOrderEvent event) {}
        });
        AlarmRecordMapper mapper = new AlarmRecordMapper();

        AlarmEvaluationService evalService = new AlarmEvaluationService(
                ruleRepo, registry, classifier, dedup, correlation, alarmRepository, publisher, mapper);

        consumer = new DeviceMetricEventConsumer(evalService);
    }

    @Test
    void shouldConsumeMetricEventAndCreateAlarm() {
        DeviceMetricEvent event = new DeviceMetricEvent("e1", "dev-1", "m1", "CPU_USAGE",
                90.0, "%", System.currentTimeMillis(), "EAST");

        consumer.onMetric(event);
        assertEquals(1, alarmRepository.findAll().size());
    }
}
