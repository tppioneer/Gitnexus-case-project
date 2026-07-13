package com.example.telecom.alarm.federated;

import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.alarm.federated.service.FederatedAlarmDeduplicationService;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FederatedAlarmDeduplicationServiceTest {

    @Mock
    private FederatedAlarmRepository alarmRepository;

    private FederatedAlarmDeduplicationService dedupService;

    @BeforeEach
    void setUp() {
        dedupService = new FederatedAlarmDeduplicationService(alarmRepository);
    }

    @Test
    void testIsDuplicate_WithSameSourceAndType() {
        LocalDateTime now = LocalDateTime.now();
        FederatedAlarmRecord existing = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now.minusMinutes(5), "Power failure", "US-EAST", null);
        existing.setAlarmId("alarm-1");
        FederatedAlarmRecord incoming = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now, "Power failure", "US-EAST", null);
        incoming.setAlarmId("alarm-2");

        when(alarmRepository.findAll()).thenReturn(List.of(existing));
        boolean result = dedupService.isDuplicate(incoming);
        assertTrue(result);
    }

    @Test
    void testIsDuplicate_WithDifferentSource() {
        LocalDateTime now = LocalDateTime.now();
        FederatedAlarmRecord existing = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now.minusMinutes(5), "Power failure", "US-EAST", null);
        existing.setAlarmId("alarm-1");
        FederatedAlarmRecord incoming = new FederatedAlarmRecord("src-2", "dev-5", "DISK_FULL",
                Severity.WARNING, now, "Disk full", "US-WEST", null);
        incoming.setAlarmId("alarm-2");

        when(alarmRepository.findAll()).thenReturn(List.of(existing));
        boolean result = dedupService.isDuplicate(incoming);
        assertFalse(result);
    }

    @Test
    void testDeduplicate() {
        LocalDateTime now = LocalDateTime.now();
        FederatedAlarmRecord alarm1 = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now.minusMinutes(1), "Power failure", "US-EAST", null);
        alarm1.setAlarmId("alarm-1");
        FederatedAlarmRecord alarm2 = new FederatedAlarmRecord("src-1", "dev-1", "POWER_FAILURE",
                Severity.CRITICAL, now, "Power failure", "US-EAST", null);
        alarm2.setAlarmId("alarm-2");
        FederatedAlarmRecord alarm3 = new FederatedAlarmRecord("src-3", "dev-3", "DISK_FULL",
                Severity.WARNING, now, "Disk full", "EU-WEST", null);
        alarm3.setAlarmId("alarm-3");

        when(alarmRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        List<FederatedAlarmRecord> deduplicated = dedupService.deduplicate(List.of(alarm1, alarm2, alarm3));
        assertNotNull(deduplicated);
    }
}
