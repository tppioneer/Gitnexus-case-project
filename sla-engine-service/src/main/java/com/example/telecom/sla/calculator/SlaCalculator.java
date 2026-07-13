package com.example.telecom.sla.calculator;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.sla.domain.SlaCalculationResult;

import java.util.List;

public interface SlaCalculator {

    SlaCalculationResult calculate(SlaContract contract, List<SlaMetricSnapshot> metrics);
}
