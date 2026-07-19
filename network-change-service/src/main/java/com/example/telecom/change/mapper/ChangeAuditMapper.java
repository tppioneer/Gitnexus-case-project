package com.example.telecom.change.mapper;

import com.example.telecom.change.domain.AuditCategory;
import com.example.telecom.change.entity.ChangeAuditEntity;
import org.springframework.stereotype.Component;

/**
 * Maps audit domain data to JPA entities.
 */
@Component
public class ChangeAuditMapper {

    public ChangeAuditEntity toEntity(String action, AuditCategory category,
                                      String operatorId, String details, boolean success) {
        return new ChangeAuditEntity(action, category, operatorId, details, success);
    }
}
