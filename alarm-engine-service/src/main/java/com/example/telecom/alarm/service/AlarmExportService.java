package com.example.telecom.alarm.service;

import com.example.telecom.alarm.dto.AlarmExportRequest;
import com.example.telecom.alarm.dto.AlarmExportResponse;
import com.example.telecom.alarm.repository.AlarmRepository;
import com.example.telecom.common.alarm.AlarmRecord;

import java.util.List;
import java.util.UUID;

public class AlarmExportService {

    private final AlarmRepository alarmRepository;

    public AlarmExportService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public AlarmExportResponse exportAlarms(AlarmExportRequest request) {
        List<AlarmRecord> alarms = alarmRepository.findAll().stream()
                .filter(a -> request.getRegionCode() == null
                        || request.getRegionCode().equals(a.getAlarmRegionCode()))
                .filter(a -> request.getSeverity() == null
                        || request.getSeverity().equals(a.getSeverity().name()))
                .toList();

        AlarmExportResponse response = new AlarmExportResponse();
        response.setJobId(UUID.randomUUID().toString());
        response.setStatus("COMPLETED");
        response.setAlarmCount(alarms.size());
        response.setFormat(request.getFormat());
        return response;
    }

    public void process(AlarmExportRequest request) {
        // Simulate async export processing
        exportAlarms(request);
    }
}
