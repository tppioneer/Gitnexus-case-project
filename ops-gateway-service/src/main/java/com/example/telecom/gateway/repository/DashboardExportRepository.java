package com.example.telecom.gateway.repository;

import com.example.telecom.gateway.domain.DashboardExportTask;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Repository
public class DashboardExportRepository {

    private final ConcurrentMap<String, DashboardExportTask> storage = new ConcurrentHashMap<>();

    public DashboardExportTask save(DashboardExportTask task) {
        storage.put(task.getExportId(), task);
        return task;
    }

    public Optional<DashboardExportTask> findById(String exportId) {
        return Optional.ofNullable(storage.get(exportId));
    }

    public List<DashboardExportTask> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<DashboardExportTask> findByStatus(String status) {
        return storage.values().stream()
                .filter(task -> status.equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    public List<DashboardExportTask> findByUserId(String userId) {
        return storage.values().stream()
                .filter(task -> userId.equals(task.getUserId()))
                .collect(Collectors.toList());
    }

    public boolean delete(String exportId) {
        return storage.remove(exportId) != null;
    }

    public long count() {
        return storage.size();
    }
}
