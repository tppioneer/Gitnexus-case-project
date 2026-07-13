package com.example.telecom.sla.calculator;

import com.example.telecom.common.sla.SlaContract;
import com.example.telecom.common.sla.SlaMetricSnapshot;
import com.example.telecom.sla.domain.SlaCalculationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SlaCalculatorRegistry {

    private final Map<String, SlaCalculator> calculators = new ConcurrentHashMap<>();

    public void register(String type, SlaCalculator calculator) {
        calculators.put(type, calculator);
    }

    public void unregister(String type) {
        calculators.remove(type);
    }

    public SlaCalculator resolve(String type) {
        return calculators.get(type);
    }

    public List<SlaCalculator> getAllCalculators() {
        return new ArrayList<>(calculators.values());
    }

    public List<SlaCalculationResult> calculateAll(SlaContract contract, List<SlaMetricSnapshot> metrics) {
        List<SlaCalculationResult> results = new ArrayList<>();
        for (SlaCalculator calculator : calculators.values()) {
            results.add(calculator.calculate(contract, metrics));
        }
        return results;
    }
}
