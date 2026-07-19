package com.example.telecom.change.mapper;

import com.example.telecom.change.domain.NetworkChangePlan;
import com.example.telecom.change.dto.ChangeRequest;
import com.example.telecom.change.dto.ChangeResponse;
import com.example.telecom.change.entity.NetworkChangeEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Maps between domain {@code NetworkChangePlan}, DTOs and JPA entities.
 */
@Component
public class NetworkChangeMapper {

    public NetworkChangePlan fromRequest(ChangeRequest request) {
        String planId = "CHG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new NetworkChangePlan(
                planId,
                request.getTitle(),
                request.getRegionCode(),
                request.getDeviceFamily(),
                request.getRisk()
        );
    }

    public NetworkChangeEntity toEntity(NetworkChangePlan plan) {
        NetworkChangeEntity entity = new NetworkChangeEntity(
                plan.getPlanId(),
                plan.getTitle(),
                plan.getRegionCode(),
                plan.getDeviceFamily(),
                plan.getRisk()
        );
        entity.setStatus(plan.getStatus());
        return entity;
    }

    public NetworkChangePlan toDomain(NetworkChangeEntity entity) {
        NetworkChangePlan plan = new NetworkChangePlan(
                entity.getChangeId(),
                entity.getTitle(),
                entity.getRegionCode(),
                entity.getDeviceFamily(),
                entity.getRisk()
        );
        plan.setStatus(entity.getStatus());
        return plan;
    }

    public ChangeResponse toResponse(NetworkChangePlan plan) {
        return new ChangeResponse(
                plan.getPlanId(),
                plan.getTitle(),
                plan.getStatus(),
                plan.getRegionCode(),
                plan.getCreatedAt()
        );
    }

    public ChangeResponse toResponse(NetworkChangeEntity entity) {
        return new ChangeResponse(
                entity.getChangeId(),
                entity.getTitle(),
                entity.getStatus(),
                entity.getRegionCode(),
                entity.getCreatedAt()
        );
    }
}
