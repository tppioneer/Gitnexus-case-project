package com.example.telecom.alarm.federated;

import com.example.telecom.alarm.federated.correlation.CorrelationRuleMatcher;
import com.example.telecom.alarm.federated.correlation.FederatedCorrelationEngine;
import com.example.telecom.alarm.federated.event.AlarmFederationEventPublisher;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.alarm.federated.service.FederatedAlarmCorrelationService;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FederatedAlarmCorrelationServiceTest {

    @Mock
    private FederatedAlarmRepository alarmRepository;

    private FederatedAlarmCorrelationService correlationService;
    private FederatedCorrelationEngine correlationEngine;
    private CorrelationRuleMatcher ruleMatcher;
    private AlarmFederationEventPublisher eventPublisher;

    private FederatedAlarmRecord alarm1;
    private FederatedAlarmRecord alarm2;
    private FederatedAlarmRecord alarm3;

    @BeforeEach
    void setUp() {
        ruleMatcher = new CorrelationRuleMatcher();
        eventPublisher = new AlarmFederationEventPublisher();
        correlationEngine = new FederatedCorrelationEngine(alarmRepository, ruleMatcher);
        correlationService = new FederatedAlarmCorrelationService(
                alarmRepository, correlationEngine, ruleMatcher, eventPublisher);

        LocalDateTime now = LocalDateTime.now();

        alarm1 = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now.minusMinutes(5), "Power failure", "US-EAST", null);
        alarm1.setAlarmId("alarm-1");
        alarm2 = new FederatedAlarmRecord("src-2", "dev-1", "POWER_FAILURE",
                Severity.MAJOR, now.minusMinutes(3), "Related power issue", "US-EAST", null);
        alarm2.setAlarmId("alarm-2");
        alarm3 = new FederatedAlarmRecord("src-3", "dev-3", "DISK_FULL",
                Severity.INFO, now.minusHours(2), "Disk space warning", "EU-WEST", null);
        alarm3.setAlarmId("alarm-3");
    }

    @Test
    void testCorrelateMatchingAlarms() {
        when(alarmRepository.findAll()).thenReturn(List.of(alarm1, alarm2, alarm3));
        when(alarmRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        List<FederatedAlarmRecord> correlated = correlationService.correlate(alarm1);
        assertNotNull(correlated);
    }

    @Test
    void testFindCorrelatedAlarms() {
        alarm1.setCorrelationGroupId("group-1");
        alarm2.setCorrelationGroupId("group-1");
        when(alarmRepository.findById("alarm-1")).thenReturn(Optional.of(alarm1));
        when(alarmRepository.findAll()).thenReturn(List.of(alarm1, alarm2, alarm3));
        List<FederatedAlarmRecord> found = correlationService.findCorrelatedAlarms("alarm-1");
        assertEquals(1, found.size());
    }

    @Test
    void testGetCorrelationScore() {
        double score = correlationService.getCorrelationScore(alarm1, alarm2);
        assertTrue(score > 0);
    }

    @Test
    void testBuildCorrelationGroup() {
        List<FederatedAlarmRecord> grouped = correlationService.buildCorrelationGroup(List.of(alarm1, alarm2, alarm3));
        assertNotNull(grouped);
    }
}
