package com.example.telecom.dispatch;

import com.example.telecom.dispatch.domain.DispatchHistory;
import com.example.telecom.dispatch.repository.DispatchHistoryRepository;
import com.example.telecom.dispatch.service.DispatchHistoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchHistoryServiceTest {

    @Mock
    private DispatchHistoryRepository dispatchHistoryRepository;

    @InjectMocks
    private DispatchHistoryService dispatchHistoryService;

    @Captor
    private ArgumentCaptor<DispatchHistory> historyCaptor;

    private DispatchHistory history1;
    private DispatchHistory history2;
    private DispatchHistory history3;
    private String orderId;
    private String operatorId;
    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        orderId = "ORD-001";
        operatorId = "OP-001";
        from = LocalDateTime.of(2024, 1, 1, 0, 0);
        to = LocalDateTime.of(2024, 12, 31, 23, 59);

        history1 = new DispatchHistory(
                UUID.randomUUID().toString(),
                orderId, "WO-001", operatorId, null,
                "ASSIGNED", LocalDateTime.of(2024, 6, 15, 10, 30),
                "Initial assignment"
        );

        history2 = new DispatchHistory(
                UUID.randomUUID().toString(),
                orderId, "WO-001", "OP-002", operatorId,
                "REASSIGNED", LocalDateTime.of(2024, 7, 20, 14, 0),
                "Reassigned to OP-002"
        );

        history3 = new DispatchHistory(
                UUID.randomUUID().toString(),
                "ORD-002", "WO-002", operatorId, null,
                "ASSIGNED", LocalDateTime.of(2024, 8, 10, 9, 0),
                "Another assignment"
        );
    }

    @Test
    void testRecordDispatch() {
        when(dispatchHistoryRepository.save(any(DispatchHistory.class))).thenReturn(history1);

        DispatchHistory result = dispatchHistoryService.recordDispatch(history1);

        assertNotNull(result);
        assertEquals(history1.getHistoryId(), result.getHistoryId());
        assertEquals(orderId, result.getOrderId());
        assertEquals("ASSIGNED", result.getAction());

        verify(dispatchHistoryRepository).save(historyCaptor.capture());
        DispatchHistory captured = historyCaptor.getValue();
        assertNotNull(captured);
        assertEquals(history1.getHistoryId(), captured.getHistoryId());
    }

    @Test
    void testGetHistoryByOrderId() {
        List<DispatchHistory> expected = Arrays.asList(history1, history2);
        when(dispatchHistoryRepository.findByOrderId(orderId)).thenReturn(expected);

        List<DispatchHistory> result = dispatchHistoryService.getHistory(orderId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(history1));
        assertTrue(result.contains(history2));
        verify(dispatchHistoryRepository).findByOrderId(orderId);
    }

    @Test
    void testGetHistoryByOperator() {
        List<DispatchHistory> expected = Arrays.asList(history1, history3);
        when(dispatchHistoryRepository.findByOperatorId(operatorId)).thenReturn(expected);

        List<DispatchHistory> result = dispatchHistoryService.getHistoryByOperator(operatorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(history1));
        assertTrue(result.contains(history3));
        verify(dispatchHistoryRepository).findByOperatorId(operatorId);
    }

    @Test
    void testGetHistoryByDateRange() {
        List<DispatchHistory> expected = Arrays.asList(history1, history2);
        when(dispatchHistoryRepository.findByDateRange(from, to)).thenReturn(expected);

        List<DispatchHistory> result = dispatchHistoryService.getHistoryByDateRange(from, to);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dispatchHistoryRepository).findByDateRange(from, to);
    }

    @Test
    void testGetRecentDispatch() {
        List<DispatchHistory> expected = Arrays.asList(history3, history2, history1);
        int limit = 3;
        when(dispatchHistoryRepository.getRecent(limit)).thenReturn(expected);

        List<DispatchHistory> result = dispatchHistoryService.getRecentDispatch(limit);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(history3.getHistoryId(), result.get(0).getHistoryId());
        assertEquals(history2.getHistoryId(), result.get(1).getHistoryId());
        assertEquals(history1.getHistoryId(), result.get(2).getHistoryId());
        verify(dispatchHistoryRepository).getRecent(limit);
    }
}
