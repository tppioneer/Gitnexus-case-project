package com.example.telecom.dispatch.service;

import com.example.telecom.dispatch.domain.DispatchHistory;
import com.example.telecom.dispatch.repository.DispatchHistoryRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DispatchHistoryService {

    private final DispatchHistoryRepository dispatchHistoryRepository;

    public DispatchHistoryService(DispatchHistoryRepository dispatchHistoryRepository) {
        this.dispatchHistoryRepository = dispatchHistoryRepository;
    }

    public DispatchHistory recordDispatch(DispatchHistory history) {
        return dispatchHistoryRepository.save(history);
    }

    public List<DispatchHistory> getHistory(String orderId) {
        return dispatchHistoryRepository.findByOrderId(orderId);
    }

    public List<DispatchHistory> getHistoryByOperator(String operatorId) {
        return dispatchHistoryRepository.findByOperatorId(operatorId);
    }

    public List<DispatchHistory> getHistoryByDateRange(LocalDateTime from, LocalDateTime to) {
        return dispatchHistoryRepository.findByDateRange(from, to);
    }

    public List<DispatchHistory> getRecentDispatch(int limit) {
        return dispatchHistoryRepository.getRecent(limit);
    }

    public List<DispatchHistory> getByOperator(String operatorId) {
        return dispatchHistoryRepository.findByOperatorId(operatorId);
    }

    public List<DispatchHistory> getByDateRange(LocalDateTime from, LocalDateTime to) {
        return dispatchHistoryRepository.findByDateRange(from, to);
    }

    public long countTotalHistory() {
        return dispatchHistoryRepository.count();
    }

    public List<DispatchHistory> getHistoryByAction(String action) {
        return dispatchHistoryRepository.findAll().stream()
                .filter(h -> action.equals(h.getAction()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<DispatchHistory> getHistoryByWorkOrder(String workOrderId) {
        return dispatchHistoryRepository.findAll().stream()
                .filter(h -> workOrderId.equals(h.getWorkOrderId()))
                .collect(java.util.stream.Collectors.toList());
    }

    public Optional<DispatchHistory> getLatestHistory(String orderId) {
        List<DispatchHistory> history = dispatchHistoryRepository.findByOrderId(orderId);
        return history.stream()
                .max((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
    }

    public boolean hasHistory(String orderId) {
        return !dispatchHistoryRepository.findByOrderId(orderId).isEmpty();
    }

    public void deleteHistory(String historyId) {
        dispatchHistoryRepository.findById(historyId);
        // Repository deletion would happen here
    }
}
