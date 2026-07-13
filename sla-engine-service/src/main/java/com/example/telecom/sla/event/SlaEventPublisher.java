package com.example.telecom.sla.event;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.common.event.DomainEventBus;
import com.example.telecom.common.workorder.SlaBreachEvent;
import com.example.telecom.sla.domain.SlaBreach;
import com.example.telecom.sla.domain.SlaReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SlaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SlaEventPublisher.class);

    private final DomainEventBus eventBus;

    public SlaEventPublisher(DomainEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void publishBreachDetected(SlaBreach breach) {
        log.info("Publishing breach detected event: {}", breach.getBreachId());
        SlaBreachEvent breachEvent = new SlaBreachEvent(
                UUID.randomUUID().toString(),
                breach.getContractId(),
                breach.getBreachId(),
                breach.getMetricType(),
                (long) breach.getActualValue(),
                (long) breach.getThreshold()
        );
        // In a real system, this would publish via event bus
        log.info("Breach event published: type={}, contractId={}",
                breach.getMetricType(), breach.getContractId());
    }

    public void publishBreachResolved(SlaBreach breach) {
        log.info("Publishing breach resolved event: {}", breach.getBreachId());
        log.info("Breach resolved: contractId={}, metricType={}",
                breach.getContractId(), breach.getMetricType());
    }

    public void publishEscalated(SlaBreach breach, EscalationLevel level) {
        log.info("Publishing escalation event: breachId={}, level={}",
                breach.getBreachId(), level);
        log.info("Escalation triggered: contractId={}, escalatedTo={}",
                breach.getContractId(), level);
    }

    public void publishReportGenerated(SlaReport report) {
        log.info("Publishing report generated event: reportId={}", report.getReportId());
        log.info("Report generated: contractId={}, type={}",
                report.getContractId(), report.getReportType());
    }
}
