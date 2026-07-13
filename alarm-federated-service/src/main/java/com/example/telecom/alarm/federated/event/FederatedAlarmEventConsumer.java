package com.example.telecom.alarm.federated.event;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import com.example.telecom.alarm.federated.service.FederatedAlarmCorrelationService;
import com.example.telecom.alarm.federated.service.FederatedAlarmEscalationService;
import com.example.telecom.alarm.federated.service.FederatedAuditService;
import org.springframework.stereotype.Component;

@Component
public class FederatedAlarmEventConsumer {

    private final FederatedAlarmCorrelationService correlationService;
    private final FederatedAlarmEscalationService escalationService;
    private final FederatedAuditService auditService;

    public FederatedAlarmEventConsumer(FederatedAlarmCorrelationService correlationService,
                                        FederatedAlarmEscalationService escalationService,
                                        FederatedAuditService auditService) {
        this.correlationService = correlationService;
        this.escalationService = escalationService;
        this.auditService = auditService;
    }

    public void onAlarmIngested(FederatedAlarmRecord record) {
        auditService.recordAction(record.getAlarmId(), "INGESTED", "SYSTEM");
        correlationService.correlate(record);
    }

    public void onAlarmCorrelated(FederatedAlarmRecord record) {
        auditService.recordAction(record.getAlarmId(), "CORRELATED", "SYSTEM");
        escalationService.escalate(record);
    }

    public void onAlarmEscalated(FederatedAlarmRecord record) {
        auditService.recordAction(record.getAlarmId(), "ESCALATED", "SYSTEM");
    }
}
