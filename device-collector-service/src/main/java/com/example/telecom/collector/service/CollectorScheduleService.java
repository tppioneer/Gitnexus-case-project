package com.example.telecom.collector.service;

import com.example.telecom.collector.dto.CollectorScheduleRequest;
import com.example.telecom.collector.dto.CollectorScheduleResponse;
import com.example.telecom.collector.repository.CollectorScheduleRepository;
import com.example.telecom.common.exception.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectorScheduleService {

    private final CollectorScheduleRepository scheduleRepository;

    public CollectorScheduleService(CollectorScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public CollectorScheduleResponse createSchedule(CollectorScheduleRequest request) {
        if (request.getScheduleName() == null || request.getScheduleName().isBlank()) {
            throw new ValidationException("scheduleName", "Schedule name is required");
        }
        if (request.getDeviceIds() == null || request.getDeviceIds().isEmpty()) {
            throw new ValidationException("deviceIds", "At least one device must be selected");
        }
        if (request.getMetricTypes() == null || request.getMetricTypes().isEmpty()) {
            throw new ValidationException("metricTypes", "At least one metric type must be selected");
        }
        if (request.getIntervalSeconds() <= 0 && (request.getCronExpression() == null || request.getCronExpression().isBlank())) {
            throw new ValidationException("intervalSeconds", "Either interval or cron expression must be provided");
        }

        CollectorSchedule schedule = new CollectorSchedule(
                UUID.randomUUID().toString(),
                request.getScheduleName(),
                request.getDeviceIds(),
                request.getMetricTypes(),
                request.getIntervalSeconds(),
                request.getCronExpression()
        );

        if (request.isEnabled()) {
            schedule.setStatus(CollectorSchedule.ScheduleStatus.RUNNING);
            schedule.setNextRunTime(System.currentTimeMillis() + request.getIntervalSeconds() * 1000L);
        }

        CollectorSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    public CollectorScheduleResponse getSchedule(String scheduleId) {
        CollectorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ValidationException("scheduleId", "Schedule not found: " + scheduleId));
        return toResponse(schedule);
    }

    public List<CollectorScheduleResponse> listSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CollectorScheduleResponse updateSchedule(String scheduleId, CollectorScheduleRequest request) {
        CollectorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ValidationException("scheduleId", "Schedule not found: " + scheduleId));

        Optional.ofNullable(request.getScheduleName())
                .filter(name -> !name.isBlank())
                .ifPresent(schedule::setScheduleName);

        Optional.ofNullable(request.getDeviceIds())
                .filter(ids -> !ids.isEmpty())
                .ifPresent(schedule::setDeviceIds);

        Optional.ofNullable(request.getMetricTypes())
                .filter(types -> !types.isEmpty())
                .ifPresent(schedule::setMetricTypes);

        if (request.getIntervalSeconds() > 0) {
            schedule.setIntervalSeconds(request.getIntervalSeconds());
        }

        Optional.ofNullable(request.getCronExpression())
                .filter(expr -> !expr.isBlank())
                .ifPresent(schedule::setCronExpression);

        CollectorSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    public void deleteSchedule(String scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ValidationException("scheduleId", "Schedule not found: " + scheduleId);
        }
        scheduleRepository.delete(scheduleId);
    }

    public CollectorScheduleResponse startSchedule(String scheduleId) {
        CollectorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ValidationException("scheduleId", "Schedule not found: " + scheduleId));

        if (schedule.getStatus() == CollectorSchedule.ScheduleStatus.RUNNING) {
            throw new ValidationException("status", "Schedule is already running");
        }

        schedule.setStatus(CollectorSchedule.ScheduleStatus.RUNNING);
        schedule.setNextRunTime(System.currentTimeMillis() + schedule.getIntervalSeconds() * 1000L);

        CollectorSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    public CollectorScheduleResponse stopSchedule(String scheduleId) {
        CollectorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ValidationException("scheduleId", "Schedule not found: " + scheduleId));

        if (schedule.getStatus() != CollectorSchedule.ScheduleStatus.RUNNING) {
            throw new ValidationException("status", "Schedule is not currently running");
        }

        schedule.setStatus(CollectorSchedule.ScheduleStatus.PAUSED);
        CollectorSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    public CollectorScheduleResponse process(String scheduleId) {
        CollectorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ValidationException("scheduleId", "Schedule not found: " + scheduleId));

        validateSchedule(schedule);

        long now = System.currentTimeMillis();
        schedule.setLastRunTime(now);
        schedule.setNextRunTime(now + schedule.getIntervalSeconds() * 1000L);
        schedule.setStatus(CollectorSchedule.ScheduleStatus.RUNNING);

        CollectorSchedule saved = scheduleRepository.save(schedule);
        return toResponse(saved);
    }

    private void validateSchedule(CollectorSchedule schedule) {
        if (schedule.getDeviceIds() == null || schedule.getDeviceIds().isEmpty()) {
            throw new ValidationException("deviceIds", "Schedule has no devices assigned");
        }
        if (schedule.getMetricTypes() == null || schedule.getMetricTypes().isEmpty()) {
            throw new ValidationException("metricTypes", "Schedule has no metric types configured");
        }
        if (schedule.getIntervalSeconds() <= 0 && (schedule.getCronExpression() == null || schedule.getCronExpression().isBlank())) {
            throw new ValidationException("intervalSeconds", "Schedule has no valid interval or cron expression");
        }
    }

    private CollectorScheduleResponse toResponse(CollectorSchedule schedule) {
        return new CollectorScheduleResponse(
                schedule.getScheduleId(),
                schedule.getScheduleName(),
                schedule.getStatus().name(),
                schedule.getDeviceCount(),
                schedule.getIntervalSeconds(),
                schedule.getLastRunTime(),
                schedule.getNextRunTime()
        );
    }
}
