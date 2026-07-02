package com.example.telecom.alarm;

import com.example.telecom.alarm.event.AlarmEventPublisher;
import com.example.telecom.alarm.mapper.AlarmRecordMapper;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.alarm.repository.ThresholdRuleRepository;
import com.example.telecom.alarm.rule.*;
import com.example.telecom.alarm.service.*;
import com.example.telecom.common.alarm.*;
import com.example.telecom.common.device.DeviceMetricEvent;
import com.example.telecom.common.device.MetricType;
import com.example.telecom.common.event.DomainEventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AlarmEvaluationServiceTest {

    private AlarmEvaluationService alarmEvaluationService;
    private ThresholdRuleRepository thresholdRuleRepository;
    private AlarmRepository alarmRepository;
    private AtomicReference<AlarmEvent> publishedEvent;

    @BeforeEach
    void setUp() {
        thresholdRuleRepository = new ThresholdRuleRepository();
        alarmRepository = new AlarmRepository();

        // Set up RuleEvaluator registry with all 5 implementations
        RuleEvaluatorRegistry registry = new RuleEvaluatorRegistry(List.of(
                new CpuUsageRuleEvaluator(),
                new MemoryUsageRuleEvaluator(),
                new OpticalPowerRuleEvaluator(),
                new PacketLossRuleEvaluator(),
                new TemperatureRuleEvaluator()
        ));

        AlarmSeverityClassifier classifier = new AlarmSeverityClassifier();
        AlarmDeduplicationService dedup = new AlarmDeduplicationService(alarmRepository);
        AlarmCorrelationService correlation = new AlarmCorrelationService(alarmRepository);

        publishedEvent = new AtomicReference<>();
        DomainEventBus bus = new DomainEventBus() {
            @Override public void publish(com.example.telecom.common.device.DeviceMetricEvent event) {}
            @Override public void publish(AlarmEvent event) { publishedEvent.set(event); }
            @Override public void publish(com.example.telecom.common.workorder.WorkOrderEvent event) {}
        };
        AlarmEventPublisher publisher = new AlarmEventPublisher(bus);
        AlarmRecordMapper mapper = new AlarmRecordMapper();

        alarmEvaluationService = new AlarmEvaluationService(
                thresholdRuleRepository, registry, classifier, dedup, correlation,
                alarmRepository, publisher, mapper);
    }

    @Test
    void shouldCreateAlarmWhenCpuExceedsThreshold() {
        thresholdRuleRepository.save(new ThresholdRule("r1", "CPU High", "CPU_USAGE", 80.0, Severity.MAJOR, true));

        DeviceMetricEvent event = new DeviceMetricEvent("e1", "dev-1", "m1", "CPU_USAGE",
                90.0, "%", System.currentTimeMillis(), "EAST");

        alarmEvaluationService.evaluate(event);

        assertEquals(1, alarmRepository.findAll().size());
        AlarmRecord alarm = alarmRepository.findAll().get(0);
        assertEquals("EAST", alarm.getAlarmRegionCode());
        assertEquals(Severity.MAJOR, alarm.getSeverity());
        assertNotNull(publishedEvent.get());
    }

    @Test
    void shouldNotCreateAlarmWhenValueNormal() {
        thresholdRuleRepository.save(new ThresholdRule("r1", "CPU Normal", "CPU_USAGE", 80.0, Severity.MAJOR, true));

        DeviceMetricEvent event = new DeviceMetricEvent("e2", "dev-1", "m2", "CPU_USAGE",
                50.0, "%", System.currentTimeMillis(), "WEST");

        alarmEvaluationService.evaluate(event);
        assertEquals(0, alarmRepository.findAll().size());
    }

    @Test
    void shouldPropagateDeviceRegionCodeToAlarmRegionCode() {
        thresholdRuleRepository.save(new ThresholdRule("r1", "CPU", "CPU_USAGE", 50.0, Severity.WARNING, true));

        DeviceMetricEvent event = new DeviceMetricEvent("e3", "dev-2", "m3", "CPU_USAGE",
                90.0, "%", System.currentTimeMillis(), "SOUTH");

        alarmEvaluationService.evaluate(event);

        AlarmRecord alarm = alarmRepository.findAll().get(0);
        // Critical Case C verification: alarmRegionCode should match the event's deviceRegionCode
        assertEquals("SOUTH", alarm.getAlarmRegionCode());
    }
}
