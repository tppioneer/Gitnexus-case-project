package com.example.telecom.common.sla;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HolidayCalendar {

    private final String calendarId;
    private final int year;
    private final Set<LocalDate> holidays;
    private final String regionCode;
    private final String description;

    public HolidayCalendar(String calendarId, int year, Set<LocalDate> holidays,
                           String regionCode, String description) {
        this.calendarId = calendarId;
        this.year = year;
        this.holidays = (holidays != null) ? Collections.unmodifiableSet(new HashSet<>(holidays)) : Collections.emptySet();
        this.regionCode = regionCode;
        this.description = description;
    }

    public String getCalendarId() { return calendarId; }
    public int getYear() { return year; }
    public Set<LocalDate> getHolidays() { return holidays; }
    public String getRegionCode() { return regionCode; }
    public String getDescription() { return description; }

    public boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }

    public long getBusinessDays(LocalDate start, LocalDate end) {
        if (start == null || end == null || start.isAfter(end)) {
            return 0;
        }
        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY
                        && date.getDayOfWeek() != DayOfWeek.SUNDAY
                        && !holidays.contains(date))
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HolidayCalendar that = (HolidayCalendar) o;
        return Objects.equals(calendarId, that.calendarId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calendarId);
    }

    @Override
    public String toString() {
        return "HolidayCalendar{" +
                "calendarId='" + calendarId + '\'' +
                ", year=" + year +
                ", holidays=" + holidays +
                ", regionCode='" + regionCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
