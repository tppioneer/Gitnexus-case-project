package com.example.telecom.alarm;

import com.example.telecom.alarm.domain.AlarmCorrelationRule;
import com.example.telecom.alarm.repository.AlarmCorrelationRuleRepository;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AlarmCorrelationRuleRepositoryTest {

    private AlarmCorrelationRuleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AlarmCorrelationRuleRepository();
    }

    @Test
    void shouldSaveAndFindById() {
        AlarmCorrelationRule rule = new AlarmCorrelationRule("cr1", "Test", "DEVICE",
                30, "dev-1", "*", Severity.WARNING, Severity.CRITICAL, true);
        repository.save(rule);

        Optional<AlarmCorrelationRule> found = repository.findById("cr1");
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());
    }

    @Test
    void shouldReturnEmptyForMissingId() {
        Optional<AlarmCorrelationRule> found = repository.findById("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAll() {
        repository.save(new AlarmCorrelationRule("cr1", "R1", "DEVICE", 10, "*", "*",
                Severity.INFO, Severity.CRITICAL, true));
        repository.save(new AlarmCorrelationRule("cr2", "R2", "TYPE", 20, "*", "*",
                Severity.WARNING, Severity.CRITICAL, true));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldFindByEnabled() {
        repository.save(new AlarmCorrelationRule("cr1", "R1", "DEVICE", 10, "*", "*",
                Severity.INFO, Severity.CRITICAL, true));
        repository.save(new AlarmCorrelationRule("cr2", "R2", "TYPE", 20, "*", "*",
                Severity.WARNING, Severity.CRITICAL, false));

        assertEquals(1, repository.findByEnabled(true).size());
        assertEquals(1, repository.findByEnabled(false).size());
    }

    @Test
    void shouldDelete() {
        repository.save(new AlarmCorrelationRule("cr1", "R1", "DEVICE", 10, "*", "*",
                Severity.INFO, Severity.CRITICAL, true));
        assertTrue(repository.delete("cr1"));
        assertEquals(0, repository.count());
    }

    @Test
    void shouldNotDeleteNonExistent() {
        assertFalse(repository.delete("nonexistent"));
    }

    @Test
    void shouldCount() {
        assertEquals(0, repository.count());
        repository.save(new AlarmCorrelationRule("cr1", "R1", "DEVICE", 10, "*", "*",
                Severity.INFO, Severity.CRITICAL, true));
        assertEquals(1, repository.count());
    }
}
