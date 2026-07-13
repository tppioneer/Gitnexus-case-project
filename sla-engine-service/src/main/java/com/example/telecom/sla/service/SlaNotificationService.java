package com.example.telecom.sla.service;

import com.example.telecom.common.EscalationLevel;
import com.example.telecom.sla.domain.SlaBreach;
import com.example.telecom.sla.domain.SlaReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SlaNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SlaNotificationService.class);

    public void notifyBreach(SlaBreach breach) {
        log.warn("NOTIFICATION: SLA Breach detected - contractId={}, metricType={}, value={}, threshold={}",
                breach.getContractId(), breach.getMetricType(),
                breach.getActualValue(), breach.getThreshold());
    }

    public void notifyEscalation(SlaBreach breach, EscalationLevel level) {
        log.warn("NOTIFICATION: SLA Breach escalated - breachId={}, contractId={}, level={}",
                breach.getBreachId(), breach.getContractId(), level);
    }

    public void notifyContractActivated(String contractId, String contractName) {
        log.info("NOTIFICATION: SLA Contract activated - contractId={}, name={}",
                contractId, contractName);
    }

    public void sendReport(SlaReport report) {
        log.info("NOTIFICATION: SLA Report sent - reportId={}, contractId={}, type={}",
                report.getReportId(), report.getContractId(), report.getReportType());
    }
}
