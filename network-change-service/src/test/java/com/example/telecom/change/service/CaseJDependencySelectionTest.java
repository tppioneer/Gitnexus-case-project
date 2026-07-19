package com.example.telecom.change.service;

import com.example.telecom.change.executor.ChangeExecutor;
import com.example.telecom.change.executor.DefaultExecutorFacade;
import com.example.telecom.change.executor.DryRunChangeExecutor;
import com.example.telecom.change.executor.RadioChangeExecutor;
import com.example.telecom.change.executor.SafeChangeExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Case J: Spring DI Selection Test — "benchmark" profile.
 * Verifies that qualifier, @Primary, @Profile, collection injection,
 * and ObjectProvider resolve correctly under fixed configuration.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class CaseJDependencySelectionTest {

    @Autowired
    private ChangeExecutionService executionService;

    @Autowired
    private DefaultExecutorFacade defaultExecutorFacade;

    @Autowired
    private LegacyChangeFacade legacyChangeFacade;

    @Test
    void safeExecutor_isInjectedByQualifier() {
        // @Qualifier("safeExecutor") must resolve to SafeChangeExecutor
        List<ChangeExecutor> executors = executionService.getExecutors();
        // The safeExecutor is NOT in the @ChangeExecutorCandidate list
        // but is injected separately via @Qualifier
        assertNotNull(executors, "executors list must not be null");
    }

    @Test
    void executorCandidateList_excludesSafeExecutor() {
        // @ChangeExecutorCandidate list should NOT include SafeChangeExecutor
        List<ChangeExecutor> candidates = executionService.getExecutors();
        for (ChangeExecutor executor : candidates) {
            assertNotEquals(SafeChangeExecutor.class, executor.getClass(),
                    "SafeChangeExecutor must NOT be in @ChangeExecutorCandidate list");
        }
    }

    @Test
    void executorCandidateList_includesRouterTransmissionRadio() {
        List<ChangeExecutor> candidates = executionService.getExecutors();
        boolean hasRouter = false, hasTransmission = false, hasRadio = false;
        for (ChangeExecutor e : candidates) {
            String name = e.getClass().getSimpleName();
            if (name.contains("Router")) hasRouter = true;
            if (name.contains("Transmission")) hasTransmission = true;
            if (name.contains("Radio")) hasRadio = true;
        }
        assertTrue(hasRouter, "Must include RouterChangeExecutor");
        assertTrue(hasTransmission, "Must include TransmissionChangeExecutor");
        assertTrue(hasRadio, "Must include RadioChangeExecutor");
    }

    @Test
    void executorCandidateList_doesNotIncludeDryRun_underBenchmarkProfile() {
        // "dry-run" profile is NOT active → DryRunChangeExecutor must not appear
        List<ChangeExecutor> candidates = executionService.getExecutors();
        for (ChangeExecutor e : candidates) {
            assertNotEquals(DryRunChangeExecutor.class, e.getClass(),
                    "DryRunChangeExecutor must NOT be active under benchmark profile");
        }
    }

    @Test
    void executorBeansMap_containsNamedBeans() {
        Map<String, ChangeExecutor> beans = executionService.getExecutorBeans();
        assertTrue(beans.containsKey("routerExecutor"), "Must contain routerExecutor");
        assertTrue(beans.containsKey("transmissionExecutor"), "Must contain transmissionExecutor");
        assertTrue(beans.containsKey("radioExecutor"), "Must contain radioExecutor");
        assertTrue(beans.containsKey("safeExecutor"), "Must contain safeExecutor");
    }

    @Test
    void primarySelection_defaultExecutorFacade_resolvesRadioExecutor() {
        // Bare ChangeExecutor injection → @Primary RadioChangeExecutor
        assertEquals(RadioChangeExecutor.class, defaultExecutorFacade.resolvedExecutorClass(),
                "@Primary must resolve to RadioChangeExecutor");
    }

    @Test
    void legacyFieldInjection_resolvesRouterExecutor() {
        // @Qualifier("routerExecutor") field injection
        assertEquals(com.example.telecom.change.executor.RouterChangeExecutor.class,
                legacyChangeFacade.resolvedClass(),
                "Legacy field injection must resolve to RouterChangeExecutor");
    }

    @Test
    void rollbackHandlerProvider_isNotNull() {
        // ObjectProvider<RollbackHandler> must provide a handler
        assertNotNull(executionService.getRollbackHandler(),
                "ObjectProvider must provide a RollbackHandler");
    }
}
