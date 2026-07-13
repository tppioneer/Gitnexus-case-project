package com.example.telecom.dispatch.service;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.dispatch.domain.DispatchContext;
import com.example.telecom.dispatch.domain.DispatchRuleResult;
import com.example.telecom.dispatch.dto.DispatchRuleRequest;
import com.example.telecom.dispatch.dto.DispatchRuleResponse;
import com.example.telecom.dispatch.mapper.DispatchMapper;
import com.example.telecom.dispatch.repository.DispatchRuleRepository;
import com.example.telecom.dispatch.repository.DispatchRuleRepository.DispatchRuleEntity;
import com.example.telecom.dispatch.rule.DispatchRuleRegistry;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DispatchRuleService {

    private final DispatchRuleRepository dispatchRuleRepository;
    private final DispatchRuleRegistry dispatchRuleRegistry;
    private final DispatchMapper dispatchMapper;

    public DispatchRuleService(DispatchRuleRepository dispatchRuleRepository,
                                DispatchRuleRegistry dispatchRuleRegistry,
                                DispatchMapper dispatchMapper) {
        this.dispatchRuleRepository = dispatchRuleRepository;
        this.dispatchRuleRegistry = dispatchRuleRegistry;
        this.dispatchMapper = dispatchMapper;
    }

    public DispatchRuleResponse createRule(DispatchRuleRequest request) {
        DispatchRuleEntity entity = new DispatchRuleEntity();
        entity.setName(request.getName());
        entity.setRuleType(request.getRuleType());
        entity.setPriority(request.getPriority());
        entity.setEnabled(request.isEnabled());
        entity.setConfiguration(request.getConfiguration());
        entity.setCreatedTime(LocalDateTime.now());

        DispatchRuleEntity saved = dispatchRuleRepository.save(entity);
        return dispatchMapper.toRuleResponse(saved);
    }

    public DispatchRuleResponse updateRule(Long ruleId, DispatchRuleRequest request) {
        String id = String.valueOf(ruleId);
        DispatchRuleEntity entity = dispatchRuleRepository.findById(id);
        if (entity == null) {
            throw new DomainException("NOT_FOUND", "Rule not found: " + ruleId);
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getRuleType() != null) {
            entity.setRuleType(request.getRuleType());
        }
        entity.setPriority(request.getPriority());
        entity.setEnabled(request.isEnabled());
        if (request.getConfiguration() != null) {
            entity.setConfiguration(request.getConfiguration());
        }
        DispatchRuleEntity saved = dispatchRuleRepository.save(entity);
        return dispatchMapper.toRuleResponse(saved);
    }

    public DispatchRuleResponse getRule(Long ruleId) {
        String id = String.valueOf(ruleId);
        DispatchRuleEntity entity = dispatchRuleRepository.findById(id);
        return dispatchMapper.toRuleResponse(entity);
    }

    public void deleteRule(Long ruleId) {
        String id = String.valueOf(ruleId);
        DispatchRuleEntity entity = dispatchRuleRepository.findById(id);
        dispatchRuleRepository.delete(id);
        dispatchRuleRegistry.unregister(entity.getName());
    }

    public List<DispatchRuleResponse> listRules() {
        List<DispatchRuleEntity> entities = dispatchRuleRepository.findAll();
        List<DispatchRuleResponse> responses = new ArrayList<>();
        for (DispatchRuleEntity entity : entities) {
            responses.add(dispatchMapper.toRuleResponse(entity));
        }
        return responses;
    }

    public void reorderRules(List<String> ruleIds) {
        dispatchRuleRepository.reorder(ruleIds);
    }

    public List<DispatchRuleResult> evaluateRules(DispatchContext context) {
        return dispatchRuleRegistry.evaluateAll(context);
    }
}
