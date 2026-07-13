package com.example.telecom.dispatch;

import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;
import com.example.telecom.dispatch.dto.DispatchRuleRequest;
import com.example.telecom.dispatch.dto.DispatchRuleResponse;
import com.example.telecom.dispatch.mapper.DispatchMapper;
import com.example.telecom.dispatch.repository.DispatchRuleRepository;
import com.example.telecom.dispatch.repository.DispatchRuleRepository.DispatchRuleEntity;
import com.example.telecom.dispatch.rule.DispatchRuleRegistry;
import com.example.telecom.dispatch.service.DispatchRuleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchRuleServiceTest {

    @Mock
    private DispatchRuleRepository dispatchRuleRepository;

    @Mock
    private DispatchRuleRegistry dispatchRuleRegistry;

    @Mock
    private DispatchMapper dispatchMapper;

    @InjectMocks
    private DispatchRuleService dispatchRuleService;

    private DispatchRuleRequest request;
    private DispatchRuleEntity entity;
    private DispatchRuleResponse response;
    private DispatchContext dispatchContext;
    private List<DispatchRuleResult> ruleResults;

    @BeforeEach
    void setUp() {
        Map<String, String> config = new HashMap<>();
        config.put("key", "value");

        request = new DispatchRuleRequest();
        request.setName("HighPriorityRule");
        request.setRuleType("PRIORITY");
        request.setPriority(1);
        request.setEnabled(true);
        request.setConfiguration(config);

        entity = new DispatchRuleEntity();
        entity.setRuleId(UUID.randomUUID().toString());
        entity.setName("HighPriorityRule");
        entity.setRuleType("PRIORITY");
        entity.setPriority(1);
        entity.setEnabled(true);
        entity.setConfiguration(config);
        entity.setCreatedTime(LocalDateTime.now());

        response = new DispatchRuleResponse();
        response.setRuleId(entity.getRuleId());
        response.setName(entity.getName());
        response.setRuleType(entity.getRuleType());
        response.setPriority(entity.getPriority());
        response.setEnabled(entity.isEnabled());
        response.setCreatedTime(entity.getCreatedTime());

        dispatchContext = mock(DispatchContext.class);

        DispatchRuleResult result1 = new DispatchRuleResult("SkillMatchRule", 85.0, true, "Skill matched");
        DispatchRuleResult result2 = new DispatchRuleResult("ProximityRule", 90.0, true, "Proximity check passed");
        ruleResults = Arrays.asList(result1, result2);
    }

    @Test
    void testCreateRule() {
        when(dispatchRuleRepository.save(any(DispatchRuleEntity.class))).thenReturn(entity);
        when(dispatchMapper.toRuleResponse(entity)).thenReturn(response);

        DispatchRuleResponse result = dispatchRuleService.createRule(request);

        assertNotNull(result);
        assertEquals("HighPriorityRule", result.getName());
        assertEquals("PRIORITY", result.getRuleType());
        assertEquals(1, result.getPriority());
        assertTrue(result.isEnabled());
        assertEquals(entity.getRuleId(), result.getRuleId());

        verify(dispatchRuleRepository).save(any(DispatchRuleEntity.class));
        verify(dispatchMapper).toRuleResponse(entity);
    }

    @Test
    void testUpdateRule() {
        Long ruleId = 1L;
        String idStr = String.valueOf(ruleId);

        when(dispatchRuleRepository.findById(idStr)).thenReturn(entity);
        when(dispatchRuleRepository.save(entity)).thenReturn(entity);
        when(dispatchMapper.toRuleResponse(entity)).thenReturn(response);

        DispatchRuleResponse result = dispatchRuleService.updateRule(ruleId, request);

        assertNotNull(result);
        assertEquals("HighPriorityRule", result.getName());
        assertEquals("PRIORITY", result.getRuleType());

        verify(dispatchRuleRepository).findById(idStr);
        verify(dispatchRuleRepository).save(entity);
        verify(dispatchMapper).toRuleResponse(entity);
    }

    @Test
    void testDeleteRule() {
        Long ruleId = 1L;
        String idStr = String.valueOf(ruleId);

        when(dispatchRuleRepository.findById(idStr)).thenReturn(entity);

        dispatchRuleService.deleteRule(ruleId);

        verify(dispatchRuleRepository).findById(idStr);
        verify(dispatchRuleRepository).delete(idStr);
        verify(dispatchRuleRegistry).unregister(entity.getName());
    }

    @Test
    void testListRules() {
        DispatchRuleEntity entity2 = new DispatchRuleEntity();
        entity2.setRuleId(UUID.randomUUID().toString());
        entity2.setName("LoadBalanceRule");
        entity2.setRuleType("LOAD_BALANCE");
        entity2.setPriority(2);
        entity2.setEnabled(true);
        entity2.setCreatedTime(LocalDateTime.now());

        DispatchRuleResponse response2 = new DispatchRuleResponse();
        response2.setRuleId(entity2.getRuleId());
        response2.setName(entity2.getName());
        response2.setRuleType(entity2.getRuleType());
        response2.setPriority(entity2.getPriority());
        response2.setEnabled(entity2.isEnabled());
        response2.setCreatedTime(entity2.getCreatedTime());

        List<DispatchRuleEntity> entities = Arrays.asList(entity, entity2);

        when(dispatchRuleRepository.findAll()).thenReturn(entities);
        when(dispatchMapper.toRuleResponse(entity)).thenReturn(response);
        when(dispatchMapper.toRuleResponse(entity2)).thenReturn(response2);

        List<DispatchRuleResponse> result = dispatchRuleService.listRules();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("HighPriorityRule", result.get(0).getName());
        assertEquals("LoadBalanceRule", result.get(1).getName());

        verify(dispatchRuleRepository).findAll();
        verify(dispatchMapper, times(2)).toRuleResponse(any(DispatchRuleEntity.class));
    }

    @Test
    void testListRules_empty() {
        when(dispatchRuleRepository.findAll()).thenReturn(Collections.emptyList());

        List<DispatchRuleResponse> result = dispatchRuleService.listRules();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(dispatchRuleRepository).findAll();
        verify(dispatchMapper, never()).toRuleResponse(any());
    }

    @Test
    void testEvaluateRules() {
        when(dispatchRuleRegistry.evaluateAll(dispatchContext)).thenReturn(ruleResults);

        List<DispatchRuleResult> results = dispatchRuleService.evaluateRules(dispatchContext);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("SkillMatchRule", results.get(0).getRuleName());
        assertTrue(results.get(0).isPassed());
        assertEquals(85.0, results.get(0).getScore());
        assertEquals("ProximityRule", results.get(1).getRuleName());
        assertEquals(90.0, results.get(1).getScore());

        verify(dispatchRuleRegistry).evaluateAll(dispatchContext);
    }

    @Test
    void testEvaluateRules_empty() {
        when(dispatchRuleRegistry.evaluateAll(dispatchContext)).thenReturn(Collections.emptyList());

        List<DispatchRuleResult> results = dispatchRuleService.evaluateRules(dispatchContext);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(dispatchRuleRegistry).evaluateAll(dispatchContext);
    }
}
