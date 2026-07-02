package com.example.telecom.alarm;

import com.example.telecom.alarm.repository.ThresholdRuleRepository;
import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.alarm.ThresholdRule;
import com.example.telecom.common.device.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ThresholdRuleRepositoryTest {

    private ThresholdRuleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ThresholdRuleRepository();

        repository.save(new ThresholdRule("r1", "CPU High", "CPU_USAGE", 90.0, Severity.CRITICAL, true));
        repository.save(new ThresholdRule("r2", "CPU Medium", "CPU_USAGE", 70.0, Severity.WARNING, true));
        repository.save(new ThresholdRule("r3", "CPU Disabled", "CPU_USAGE", 95.0, Severity.CRITICAL, false));
        repository.save(new ThresholdRule("r4", "Memory High", "MEMORY_USAGE", 85.0, Severity.MAJOR, true));
    }

    @Test
    void shouldLoadEnabledRulesByMetricType() {
        List<ThresholdRule> cpuRules = repository.loadRules(MetricType.CPU_USAGE);
        assertEquals(2, cpuRules.size());
        assertTrue(cpuRules.stream().allMatch(ThresholdRule::isEnabled));
    }

    @Test
    void shouldExcludeDisabledRules() {
        List<ThresholdRule> cpuRules = repository.loadRules(MetricType.CPU_USAGE);
        assertTrue(cpuRules.stream().noneMatch(r -> r.getRuleId().equals("r3")));
    }

    @Test
    void shouldReturnEmptyForMetricTypeWithNoRules() {
        List<ThresholdRule> rules = repository.loadRules(MetricType.OPTICAL_POWER);
        assertTrue(rules.isEmpty());
    }

    @Test
    void shouldFindRuleById() {
        assertTrue(repository.findById("r1").isPresent());
        assertEquals("CPU High", repository.findById("r1").get().getRuleName());
    }

    @Test
    void shouldReturnEmptyForMissingId() {
        assertTrue(repository.findById("nonexistent").isEmpty());
    }

    @Test
    void shouldFindAllRules() {
        assertEquals(4, repository.findAll().size());
    }
}
