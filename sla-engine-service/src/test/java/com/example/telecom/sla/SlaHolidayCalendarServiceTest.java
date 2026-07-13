package com.example.telecom.sla;

import com.example.telecom.sla.service.SlaHolidayCalendarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SlaHolidayCalendarServiceTest {

    private SlaHolidayCalendarService holidayCalendarService;

    @BeforeEach
    void setUp() {
        holidayCalendarService = new SlaHolidayCalendarService();
    }

    @Test
    void isHoliday_shouldReturnTrue_forAddedHoliday() {
        LocalDate holidayDate = LocalDate.of(2026, 12, 25);
        holidayCalendarService.addHoliday(holidayDate, "Christmas");

        assertTrue(holidayCalendarService.isHoliday(holidayDate));
    }

    @Test
    void isHoliday_shouldReturnFalse_forRegularDay() {
        LocalDate regularDate = LocalDate.of(2026, 7, 15);
        assertFalse(holidayCalendarService.isHoliday(regularDate));
    }

    @Test
    void getBusinessDays_shouldExcludeWeekendsAndHolidays() {
        LocalDate monday = LocalDate.of(2026, 7, 6);
        LocalDate friday = LocalDate.of(2026, 7, 10);

        long businessDays = holidayCalendarService.getBusinessDays(monday, friday);

        // Monday to Friday = 5 business days (no holidays in this range)
        assertEquals(5, businessDays);
    }

    @Test
    void getBusinessDays_shouldExcludeAddedHoliday() {
        LocalDate wednesday = LocalDate.of(2026, 7, 8);
        holidayCalendarService.addHoliday(wednesday, "Company Holiday");

        LocalDate monday = LocalDate.of(2026, 7, 6);
        LocalDate friday = LocalDate.of(2026, 7, 10);

        long businessDays = holidayCalendarService.getBusinessDays(monday, friday);

        // Mon Tue Thu Fri = 4 business days (Wed is holiday)
        assertEquals(4, businessDays);
    }

    @Test
    void removeHoliday_shouldRemoveFromCalendar() {
        LocalDate holidayDate = LocalDate.of(2026, 12, 25);
        holidayCalendarService.addHoliday(holidayDate, "Christmas");

        assertTrue(holidayCalendarService.isHoliday(holidayDate));

        holidayCalendarService.removeHoliday(holidayDate);

        assertFalse(holidayCalendarService.isHoliday(holidayDate));
    }
}
