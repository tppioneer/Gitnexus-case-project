package com.example.telecom.alarm.federated.correlation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FederatedCorrelationEngineTest {

    @Mock
    private FederatedAlarmRepository alarmRepository;

    private FederatedCorrelationEngine correlationEngine;
    private CorrelationRuleMatcher ruleMatcher;

    private FederatedAlarmRecord alarm1;
    private FederatedAlarmRecord alarm2;
    private FederatedAlarmRecord alarm3;

    @BeforeEach
    void setUp() {
        ruleMatcher = new CorrelationRuleMatcher();
        correlationEngine = new FederatedCorrelationEngine(alarmRepository, ruleMatcher);

        LocalDateTime now = LocalDateTime.now();

        alarm1 = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now.minusMinutes(5), "Power failure", "US-EAST", null);
        alarm1.setAlarmId("alarm-1");
        alarm2 = new FederatedAlarmRecord("src-2", "dev-1", "POWER_FAILURE",
                Severity.MAJOR, now.minusMinutes(3), "Related power issue", "US-EAST", null);
        alarm2.setAlarmId("alarm-2");
        alarm3 = new FederatedAlarmRecord("src-3", "dev-3", "DISK_FULL",
                Severity.INFO, now.minusHours(5), "Disk space", "EU-WEST", null);
        alarm3.setAlarmId("alarm-3");
    }

    @Test
    void testFindCorrelationCandidates() {
        when(alarmRepository.findAll()).thenReturn(List.of(alarm1, alarm2, alarm3));
        List<FederatedAlarmRecord> candidates = correlationEngine.findCorrelationCandidates(alarm2);
        assertFalse(candidates.isEmpty());
    }

    @Test
    void testGetCorrelationConfidence() {
        double confidence = correlationEngine.getCorrelationConfidence(alarm1, alarm2);
        assertTrue(confidence > 0);
    }

    @Test
    void testBuildCorrelationGraph() {
        alarm1.setCorrelationGroupId("group-test");
        alarm2.setCorrelationGroupId("group-test");
        Map<String, List<FederatedAlarmRecord>> graph = correlationEngine.buildCorrelationGraph(
                List.of(alarm1, alarm2, alarm3));
        assertTrue(graph.containsKey("group-test"));
        assertEquals(2, graph.get("group-test").size());
    }
}
