package com.example.telecom.workorder.assignment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssigneeSelectorRegistry {

    private final Map<String, AssigneeSelector> selectorMap;
    private final AssigneeSelector defaultSelector;

    public AssigneeSelectorRegistry(List<AssigneeSelector> selectors) {
        this.selectorMap = selectors.stream()
                .collect(Collectors.toMap(
                        s -> s.getClass().getSimpleName().replace("AssigneeSelector", ""),
                        s -> s
                ));
        this.defaultSelector = selectors.stream()
                .filter(s -> s instanceof LoadBalancedAssigneeSelector)
                .findFirst()
                .orElse(selectors.get(0));
    }

    public AssigneeSelector resolve(String strategy) {
        return selectorMap.getOrDefault(strategy, defaultSelector);
    }
}
