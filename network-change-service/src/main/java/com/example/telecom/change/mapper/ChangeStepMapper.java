package com.example.telecom.change.mapper;

import com.example.telecom.change.entity.ChangeStepEntity;
import org.springframework.stereotype.Component;

/**
 * Maps step domain data to JPA entities.
 */
@Component
public class ChangeStepMapper {

    public ChangeStepEntity toEntity(int order, String stepName, String executorBean) {
        return new ChangeStepEntity(order, stepName, executorBean);
    }
}
