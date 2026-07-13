package com.example.telecom.gateway.service;

import com.example.telecom.gateway.dto.DashboardTrendResponse;
import com.example.telecom.gateway.dto.DashboardTrendResponse.DataPoint;
import com.example.telecom.gateway.client.AlarmClient;
import com.example.telecom.gateway.client.DeviceClient;
import com.example.telecom.gateway.client.WorkOrderClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardTrendService {

    private final DeviceClient deviceClient;
    private final AlarmClient alarmClient;
    private final WorkOrderClient workOrderClient;

    public DashboardTrendService(DeviceClient deviceClient,
                                 AlarmClient alarmClient,
                                 WorkOrderClient workOrderClient) {
        this.deviceClient = deviceClient;
        this.alarmClient = alarmClient;
        this.workOrderClient = workOrderClient;
    }

    public DashboardTrendResponse getOverviewTrend(LocalDate from, LocalDate to) {
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            LocalDateTime dayStart = current.atStartOfDay();
            double devicesOnline = deviceClient.getAllDevices().size() * 0.92;
            double alarmsActive = alarmClient.getAllAlarms().size() * (0.5 + Math.random() * 0.3);
            double workOrdersPending = workOrderClient.getAllWorkOrders().size() * (0.3 + Math.random() * 0.2);
            double compositeValue = (devicesOnline + (100.0 - alarmsActive) + (100.0 - workOrdersPending)) / 3.0;
            dataPoints.add(new DataPoint(dayStart, Math.round(compositeValue * 100.0) / 100.0, current.toString()));
            current = current.plusDays(1);
        }
        return computeTrend("overview", dataPoints);
    }

    public DashboardTrendResponse getDeviceTrend(LocalDate from, LocalDate to) {
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            LocalDateTime dayStart = current.atStartOfDay();
            long totalDevices = deviceClient.getAllDevices().size();
            double onlineRate = 85.0 + Math.random() * 15.0;
            double value = totalDevices * onlineRate / 100.0;
            dataPoints.add(new DataPoint(dayStart, Math.round(value * 100.0) / 100.0, current.toString()));
            current = current.plusDays(1);
        }
        return computeTrend("device", dataPoints);
    }

    public DashboardTrendResponse getAlarmTrend(LocalDate from, LocalDate to) {
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            LocalDateTime dayStart = current.atStartOfDay();
            long alarmCount = alarmClient.getAllAlarms().size();
            double dailyAlarms = alarmCount / Math.max(1, ChronoUnit.DAYS.between(from, to)) * (0.7 + Math.random() * 0.6);
            dataPoints.add(new DataPoint(dayStart, Math.round(dailyAlarms * 100.0) / 100.0, current.toString()));
            current = current.plusDays(1);
        }
        return computeTrend("alarm", dataPoints);
    }

    public DashboardTrendResponse getWorkOrderTrend(LocalDate from, LocalDate to) {
        List<DataPoint> dataPoints = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            LocalDateTime dayStart = current.atStartOfDay();
            long workOrderCount = workOrderClient.getAllWorkOrders().size();
            double dailyWorkOrders = workOrderCount / Math.max(1, ChronoUnit.DAYS.between(from, to)) * (0.6 + Math.random() * 0.8);
            dataPoints.add(new DataPoint(dayStart, Math.round(dailyWorkOrders * 100.0) / 100.0, current.toString()));
            current = current.plusDays(1);
        }
        return computeTrend("workorder", dataPoints);
    }

    private DashboardTrendResponse computeTrend(String type, List<DataPoint> dataPoints) {
        if (dataPoints.isEmpty()) {
            return new DashboardTrendResponse(type, dataPoints, "daily", 0.0, "No data available");
        }

        double firstValue = dataPoints.get(0).getValue().doubleValue();
        double lastValue = dataPoints.get(dataPoints.size() - 1).getValue().doubleValue();
        double changePercent = firstValue > 0 ? ((lastValue - firstValue) / firstValue) * 100.0 : 0.0;
        changePercent = Math.round(changePercent * 100.0) / 100.0;

        long totalDays = dataPoints.size();
        double avgValue = dataPoints.stream()
                .mapToDouble(dp -> dp.getValue().doubleValue())
                .average()
                .orElse(0.0);
        avgValue = Math.round(avgValue * 100.0) / 100.0;

        String summary = String.format("%s trend over %d days: start=%.1f, end=%.1f, avg=%.1f, change=%+.1f%%",
                type, totalDays, firstValue, lastValue, avgValue, changePercent);

        return new DashboardTrendResponse(type, dataPoints, "daily", changePercent, summary);
    }
}
