package com.example.telecom.collector.repository;

import com.example.telecom.collector.service.CollectorSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CollectorScheduleRepository {

    private final Map<String, CollectorSchedule> schedules = new ConcurrentHashMap<>();

    public CollectorSchedule save(CollectorSchedule schedule) {
        if (schedule.getScheduleId() == null) {
            throw new IllegalArgumentException("Schedule ID must not be null");
        }
        schedules.put(schedule.getScheduleId(), schedule);
        return schedule;
    }

    public Optional<CollectorSchedule> findById(String scheduleId) {
        return Optional.ofNullable(schedules.get(scheduleId));
    }

    public List<CollectorSchedule> findAll() {
        return new ArrayList<>(schedules.values());
    }

    public List<CollectorSchedule> findByStatus(CollectorSchedule.ScheduleStatus status) {
        return schedules.values().stream()
                .filter(s -> s.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<CollectorSchedule> findActive() {
        return schedules.values().stream()
                .filter(s -> s.getStatus() == CollectorSchedule.ScheduleStatus.RUNNING)
                .collect(Collectors.toList());
    }

    public boolean delete(String scheduleId) {
        return schedules.remove(scheduleId) != null;
    }

    public long count() {
        return schedules.size();
    }

    public long countByStatus(CollectorSchedule.ScheduleStatus status) {
        return schedules.values().stream()
                .filter(s -> s.getStatus() == status)
                .count();
    }

    public boolean existsById(String scheduleId) {
        return schedules.containsKey(scheduleId);
    }

    public List<CollectorSchedule> findOverdueSchedules(long currentTime) {
        return schedules.values().stream()
                .filter(s -> s.getStatus() == CollectorSchedule.ScheduleStatus.RUNNING)
                .filter(s -> s.getNextRunTime() > 0 && s.getNextRunTime() <= currentTime)
                .collect(Collectors.toList());
    }

    public void clear() {
        schedules.clear();
    }
}
