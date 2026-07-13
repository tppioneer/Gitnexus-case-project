package com.example.telecom.alarm;

import com.example.telecom.alarm.domain.AlarmSuppressionRule;
import com.example.telecom.alarm.repository.AlarmSuppressionRuleRepository;
import com.example.telecom.common.alarm.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AlarmSuppressionRuleRepositoryTest {

    private AlarmSuppressionRuleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AlarmSuppressionRuleRepository();
    }

    @Test
    void shouldSaveAndFindById() {
        AlarmSuppressionRule rule = new AlarmSuppressionRule("r1", "Test", "dev-*", "CPU",
                Severity.MAJOR, 30, true, System.currentTimeMillis());
        repository.save(rule);

        Optional<AlarmSuppressionRule> found = repository.findById("r1");
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());
    }

    @Test
    void shouldReturnEmptyForMissingId() {
        Optional<AlarmSuppressionRule> found = repository.findById("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAllRules() {
        repository.save(new AlarmSuppressionRule("r1", "R1", "*", "*", Severity.INFO, 10, true, 1L));
        repository.save(new AlarmSuppressionRule("r2", "R2", "*", "*", Severity.INFO, 10, true, 2L));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldFindByEnabled() {
        repository.save(new AlarmSuppressionRule("r1", "R1", "*", "*", Severity.INFO, 10, true, 1L));
        repository.save(new AlarmSuppressionRule("r2", "R2", "*", "*", Severity.INFO, 10, false, 2L));

        assertEquals(1, repository.findByEnabled(true).size());
        assertEquals(1, repository.findByEnabled(false).size());
    }

    @Test
    void shouldFindMatchingRules() {
        repository.save(new AlarmSuppressionRule("r1", "R1", "dev-1", "CPU", Severity.CRITICAL, 30, true, 1L));

        List<AlarmSuppressionRule> matches = repository.findMatchingRules("dev-1", "CPU_USAGE");
        assertEquals(1, matches.size());
    }

    @Test
    void shouldNotFindNonMatchingRules() {
        repository.save(new AlarmSuppressionRule("r1", "R1", "dev-2", "MEMORY", Severity.CRITICAL, 30, true, 1L));

        List<AlarmSuppressionRule> matches = repository.findMatchingRules("dev-1", "CPU_USAGE");
        assertTrue(matches.isEmpty());
    }

    @Test
    void shouldDeleteRule() {
        repository.save(new AlarmSuppressionRule("r1", "R1", "*", "*", Severity.INFO, 10, true, 1L));
        assertTrue(repository.delete("r1"));
        assertEquals(0, repository.count());
    }

    @Test
    void shouldReturnFalseForDeleteNonExistent() {
        assertFalse(repository.delete("nonexistent"));
    }

    @Test
    void shouldCountRules() {
        assertEquals(0, repository.count());
        repository.save(new AlarmSuppressionRule("r1", "R1", "*", "*", Severity.INFO, 10, true, 1L));
        assertEquals(1, repository.count());
    }
}
