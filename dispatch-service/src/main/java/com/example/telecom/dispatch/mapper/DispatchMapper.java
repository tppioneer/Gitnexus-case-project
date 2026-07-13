package com.example.telecom.dispatch.mapper;

import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.dispatch.domain.DispatchHistory;
import com.example.telecom.dispatch.dto.DispatchRequest;
import com.example.telecom.dispatch.dto.DispatchResponse;
import com.example.telecom.dispatch.dto.DispatchRuleResponse;
import com.example.telecom.dispatch.repository.DispatchRuleRepository.DispatchRuleEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class DispatchMapper {

    private static final Logger log = LoggerFactory.getLogger(DispatchMapper.class);

    public DispatchResponse toResponse(DispatchOrder order) {
        DispatchResponse response = new DispatchResponse();
        response.setOrderId(order.getOrderId());
        response.setWorkOrderId(order.getWorkOrderId());
        response.setAssigneeId(order.getAssigneeId());
        response.setPriority(order.getPriority());
        response.setStatus(order.getStatus());
        if (order.getAssignedTime() != null) {
            response.setAssignedTime(order.getAssignedTime());
        } else {
            response.setAssignedTime(LocalDateTime.now());
        }
        return response;
    }

    public Map<String, Object> toEvent(DispatchOrder order) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getOrderId());
        event.put("workOrderId", order.getWorkOrderId());
        event.put("priority", order.getPriority());
        event.put("targetRegionCode", order.getTargetRegionCode());
        event.put("assigneeId", order.getAssigneeId());
        event.put("skillRequired", order.getSkillRequired());
        event.put("estimatedDuration", order.getEstimatedDuration());
        event.put("status", order.getStatus());
        event.put("createdTime", order.getCreatedTime());
        event.put("assignedTime", order.getAssignedTime());
        event.put("completedTime", order.getCompletedTime());
        return event;
    }

    public DispatchRuleResponse toRuleResponse(DispatchRuleEntity entity) {
        DispatchRuleResponse response = new DispatchRuleResponse();
        response.setRuleId(entity.getRuleId());
        response.setName(entity.getName());
        response.setRuleType(entity.getRuleType());
        response.setPriority(entity.getPriority());
        response.setEnabled(entity.isEnabled());
        response.setCreatedTime(entity.getCreatedTime());
        return response;
    }

    public Map<String, Object> toHistoryResponse(DispatchHistory history) {
        Map<String, Object> map = new HashMap<>();
        map.put("historyId", history.getHistoryId());
        map.put("orderId", history.getOrderId());
        map.put("workOrderId", history.getWorkOrderId());
        map.put("action", history.getAction());
        map.put("assigneeId", history.getAssigneeId());
        map.put("previousAssigneeId", history.getPreviousAssigneeId());
        map.put("details", history.getDetails());
        map.put("timestamp", history.getTimestamp());
        return map;
    }

    public void updateFromRequest(DispatchOrder order, DispatchRequest request) {
        log.warn("updateFromRequest: DispatchOrder is immutable, cannot modify. OrderId={}",
                order.getOrderId());
    }

    public void copyFields(DispatchOrder source, DispatchOrder target) {
        log.warn("copyFields: DispatchOrder is immutable, cannot copy fields from {} to {}",
                source.getOrderId(), target.getOrderId());
    }
}
