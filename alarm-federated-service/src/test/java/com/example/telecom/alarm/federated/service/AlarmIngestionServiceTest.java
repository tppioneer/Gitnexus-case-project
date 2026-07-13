package com.example.telecom.alarm.federated.service;

import com.example.telecom.alarm.federated.*;
import com.example.telecom.alarm.federated.event.AlarmFederationEventPublisher;
import com.example.telecom.alarm.federated.repository.FederatedAlarmRepository;
import com.example.telecom.alarm.federated.repository.FederatedAlarmSourceRepository;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.common.exception.ValidationException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmIngestionServiceTest {

    @Mock
    private FederatedAlarmRepository alarmRepository;

    @Mock
    private FederatedAlarmSourceRepository sourceRepository;

    @Mock
    private FederatedAlarmSourceRegistry sourceRegistry;

    @Mock
    private FederatedAlarmDeduplicationService dedupService;

    @Mock
    private DomainEventBus domainEventBus;

    private AlarmIngestionService ingestionService;
    private AlarmFederationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new AlarmFederationEventPublisher();
        ingestionService = new AlarmIngestionService(
                alarmRepository, sourceRepository, sourceRegistry, dedupService, eventPublisher);
    }

    @Test
    void testIngest_ValidRequest() {
        FederatedAlarmRequest request = new FederatedAlarmRequest(
                "src-1", "dev-1", "POWER_FAILURE", "CRITICAL",
                LocalDateTime.now(), "Power failure detected", "US-EAST", null);

        when(sourceRepository.findById("src-1")).thenReturn(Optional.of(new FederatedAlarmSource("src-1", "Source 1", "US-EAST", "")));
        when(dedupService.isDuplicate(any())).thenReturn(false);
        when(alarmRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FederatedAlarmRecord result = ingestionService.ingest(request);

        assertNotNull(result);
        assertNotNull(result.getAlarmId());
        assertEquals("src-1", result.getSourceId());
        assertEquals("dev-1", result.getDeviceId());
        assertEquals(Severity.CRITICAL, result.getSeverity());
        assertEquals(FederatedAlarmStatus.NEW, result.getStatus());
    }

    @Test
    void testIngest_InvalidRequest_ThrowsValidationException() {
        FederatedAlarmRequest request = new FederatedAlarmRequest(
                null, "dev-1", "POWER_FAILURE", "CRITICAL",
                LocalDateTime.now(), "desc", "US-EAST", null);

        assertThrows(ValidationException.class, () -> ingestionService.ingest(request));
    }

    @Test
    void testIngestBatch() {
        FederatedAlarmRequest req1 = new FederatedAlarmRequest(
                "src-1", "dev-1", "POWER_FAILURE", "CRITICAL",
                LocalDateTime.now(), "desc1", "US-EAST", null);
        FederatedAlarmRequest req2 = new FederatedAlarmRequest(
                "src-2", "dev-2", "TEMP_HIGH", "MAJOR",
                LocalDateTime.now(), "desc2", "US-WEST", null);

        when(sourceRepository.findById("src-1")).thenReturn(Optional.of(new FederatedAlarmSource("src-1", "S1", "US-EAST", "")));
        when(sourceRepository.findById("src-2")).thenReturn(Optional.of(new FederatedAlarmSource("src-2", "S2", "US-WEST", "")));
        when(dedupService.isDuplicate(any())).thenReturn(false);
        when(alarmRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        List<FederatedAlarmRecord> results = ingestionService.ingestBatch(List.of(req1, req2));

        assertEquals(2, results.size());
    }
}
