package com.example.telecom.common.sla;

import com.example.telecom.common.region.ShiftType;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class ShiftSchedule {

    private final String scheduleId;
    private final String userId;
    private final ShiftType shiftType;
    private final String regionCode;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public ShiftSchedule(String scheduleId, String userId, ShiftType shiftType,
                         String regionCode, LocalDate date,
                         LocalTime startTime, LocalTime endTime) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.shiftType = shiftType;
        this.regionCode = regionCode;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getScheduleId() { return scheduleId; }
    public String getUserId() { return userId; }
    public ShiftType getShiftType() { return shiftType; }
    public String getRegionCode() { return regionCode; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }

    public boolean isActive(LocalDateTime time) {
        if (time == null || date == null || startTime == null || endTime == null) {
            return false;
        }
        LocalDateTime shiftStart = LocalDateTime.of(date, startTime);
        LocalDateTime shiftEnd = LocalDateTime.of(date, endTime);
        return !time.isBefore(shiftStart) && !time.isAfter(shiftEnd);
    }

    public long getDurationHours() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return Duration.between(startTime, endTime).toHours();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftSchedule that = (ShiftSchedule) o;
        return Objects.equals(scheduleId, that.scheduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId);
    }

    @Override
    public String toString() {
        return "ShiftSchedule{" +
                "scheduleId='" + scheduleId + '\'' +
                ", userId='" + userId + '\'' +
                ", shiftType=" + shiftType +
                ", regionCode='" + regionCode + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
