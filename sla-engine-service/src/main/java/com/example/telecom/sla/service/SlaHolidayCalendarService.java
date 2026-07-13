package com.example.telecom.sla.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SlaHolidayCalendarService {

    private static final Logger log = LoggerFactory.getLogger(SlaHolidayCalendarService.class);

    private final Map<LocalDate, String> holidays = new ConcurrentHashMap<>();

    public boolean isHoliday(LocalDate date) {
        return holidays.containsKey(date);
    }

    public long getBusinessDays(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            return 0;
        }

        long businessDays = 0;
        LocalDate current = start;

        while (!current.isAfter(end)) {
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY
                    && current.getDayOfWeek() != DayOfWeek.SUNDAY
                    && !holidays.containsKey(current)) {
                businessDays++;
            }
            current = current.plusDays(1);
        }

        return businessDays;
    }

    public void addHoliday(LocalDate date, String description) {
        holidays.put(date, description);
        log.info("Holiday added: date={}, description={}", date, description);
    }

    public void removeHoliday(LocalDate date) {
        holidays.remove(date);
        log.info("Holiday removed: date={}", date);
    }

    public Map<LocalDate, String> getCalendar(int year) {
        return holidays.entrySet().stream()
                .filter(entry -> entry.getKey().getYear() == year)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
