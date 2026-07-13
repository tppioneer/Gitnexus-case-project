package com.example.telecom.sla.monitor;

import com.example.telecom.common.alarm.Severity;
import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import org.springframework.stereotype.Component;

@Component
public class SlaBreachPolicy {

    public boolean evaluate(SlaContract contract, SlaMetricSnapshot snapshot) {
        return isBreach(contract, snapshot);
    }

    public boolean isBreach(SlaContract contract, SlaMetricSnapshot snapshot) {
        switch (snapshot.getMetricType()) {
            case "response_time":
                return snapshot.getMetricValue() > contract.getResponseTimeThreshold();
            case "resolution_time":
                return snapshot.getMetricValue() > contract.getResolutionTimeThreshold();
            case "availability":
                return snapshot.getMetricValue() < contract.getAvailabilityTarget();
            default:
                return snapshot.getMetricValue() > snapshot.getThreshold();
        }
    }

    public Severity getBreachSeverity(SlaContract contract, SlaMetricSnapshot snapshot) {
        double deviation;
        switch (snapshot.getMetricType()) {
            case "response_time":
                deviation = (snapshot.getMetricValue() - contract.getResponseTimeThreshold())
                        / (double) contract.getResponseTimeThreshold();
                break;
            case "resolution_time":
                deviation = (snapshot.getMetricValue() - contract.getResolutionTimeThreshold())
                        / (double) contract.getResolutionTimeThreshold();
                break;
            case "availability":
                deviation = (contract.getAvailabilityTarget() - snapshot.getMetricValue())
                        / contract.getAvailabilityTarget();
                break;
            default:
                deviation = (snapshot.getMetricValue() - snapshot.getThreshold())
                        / snapshot.getThreshold();
                break;
        }

        if (deviation > 0.5) {
            return Severity.CRITICAL;
        } else if (deviation > 0.3) {
            return Severity.MAJOR;
        } else if (deviation > 0.1) {
            return Severity.WARNING;
        } else {
            return Severity.INFO;
        }
    }

    public String getBreachDescription(SlaContract contract, SlaMetricSnapshot snapshot) {
        return String.format("SLA breach detected for contract '%s' (vendor: %s): metric '%s' "
                        + "value %.2f exceeds threshold %.2f",
                contract.getContractName(), contract.getVendorId(),
                snapshot.getMetricType(), snapshot.getMetricValue(),
                getThresholdForMetric(contract, snapshot));
    }

    private double getThresholdForMetric(SlaContract contract, SlaMetricSnapshot snapshot) {
        switch (snapshot.getMetricType()) {
            case "response_time":
                return contract.getResponseTimeThreshold();
            case "resolution_time":
                return contract.getResolutionTimeThreshold();
            case "availability":
                return contract.getAvailabilityTarget();
            default:
                return snapshot.getThreshold();
        }
    }
}
