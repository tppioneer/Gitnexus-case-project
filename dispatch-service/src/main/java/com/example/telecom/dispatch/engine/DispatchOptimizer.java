package com.example.telecom.dispatch.engine;

import com.example.telecom.common.dispatch.DispatchOrder;
import com.example.telecom.common.dispatch.DispatchPriority;
import com.example.telecom.dispatch.domain.Operator;
import com.example.telecom.dispatch.service.DispatchScoringService;
import com.example.telecom.dispatch.service.OperatorAvailabilityService;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DispatchOptimizer {

    private final DispatchEngine dispatchEngine;
    private final DispatchScoringService scoringService;
    private final OperatorAvailabilityService availabilityService;

    public DispatchOptimizer(DispatchEngine dispatchEngine,
                             DispatchScoringService scoringService,
                             OperatorAvailabilityService availabilityService) {
        this.dispatchEngine = dispatchEngine;
        this.scoringService = scoringService;
        this.availabilityService = availabilityService;
    }

    public List<DispatchOrder> optimize(List<DispatchOrder> orders) {
        List<DispatchOrder> results = new ArrayList<>();
        for (DispatchOrder order : orders) {
            results.add(dispatchEngine.dispatch(order));
        }
        return results;
    }

    public Map<String, List<DispatchOrder>> optimizeSchedule(List<DispatchOrder> orders,
                                                              List<String> operators) {
        Map<String, List<DispatchOrder>> schedule = new LinkedHashMap<>();
        for (String op : operators) {
            schedule.put(op, new ArrayList<>());
        }

        if (operators.isEmpty()) {
            return schedule;
        }

        List<DispatchOrder> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(Comparator.comparingInt(o -> o.getPriority().ordinal()));

        int operatorIndex = 0;
        for (DispatchOrder order : sortedOrders) {
            Map<String, Double> scores = new HashMap<>();
            for (String opId : operators) {
                List<Operator> allOperators = availabilityService.getAvailableOperators();
                Operator operator = findOperator(allOperators, opId);
                if (operator != null) {
                    scores.put(opId, scoringService.calculateScore(order, operator));
                } else {
                    scores.put(opId, 0.0);
                }
            }

            String bestOperator = null;
            double bestScore = -1;
            for (Map.Entry<String, Double> entry : scores.entrySet()) {
                if (entry.getValue() > bestScore) {
                    bestScore = entry.getValue();
                    bestOperator = entry.getKey();
                }
            }

            if (bestOperator != null) {
                List<DispatchOrder> operatorOrders = schedule.get(bestOperator);
                if (operatorOrders != null) {
                    operatorOrders.add(order);
                }
            } else {
                String roundRobinOperator = operators.get(operatorIndex % operators.size());
                schedule.get(roundRobinOperator).add(order);
                operatorIndex++;
            }
        }

        return schedule;
    }

    public Map<String, String> calculateOptimalAssignment(List<DispatchOrder> orders) {
        Map<String, String> assignment = new HashMap<>();
        List<Operator> allOperators = availabilityService.getAvailableOperators();

        for (DispatchOrder order : orders) {
            String bestOperator = null;
            double bestScore = -1;

            for (Operator op : allOperators) {
                double score = scoringService.calculateScore(order, op);
                if (score > bestScore) {
                    bestScore = score;
                    bestOperator = op.getId();
                }
            }

            if (bestOperator != null) {
                assignment.put(order.getOrderId(), bestOperator);
            }
        }

        return assignment;
    }

    public List<DispatchOrder> balanceWorkload(List<DispatchOrder> orders) {
        List<DispatchOrder> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort((o1, o2) -> {
            int priorityCompare = Integer.compare(
                    o2.getPriority().ordinal(), o1.getPriority().ordinal());
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            return Integer.compare(o1.getEstimatedDuration(), o2.getEstimatedDuration());
        });

        List<DispatchOrder> balanced = new ArrayList<>();
        for (DispatchOrder order : sortedOrders) {
            String leastLoaded = findLeastLoadedOperator();
            if (leastLoaded != null) {
                DispatchOrder assigned = DispatchOrder.builder()
                        .orderId(order.getOrderId())
                        .workOrderId(order.getWorkOrderId())
                        .priority(order.getPriority())
                        .targetRegionCode(order.getTargetRegionCode())
                        .assigneeId(leastLoaded)
                        .skillRequired(order.getSkillRequired())
                        .estimatedDuration(order.getEstimatedDuration())
                        .status("ASSIGNED")
                        .createdTime(order.getCreatedTime())
                        .assignedTime(order.getAssignedTime())
                        .completedTime(order.getCompletedTime())
                        .build();
                balanced.add(assigned);
            } else {
                balanced.add(order);
            }
        }

        return balanced;
    }

    private String findLeastLoadedOperator() {
        List<Operator> operators = availabilityService.getAvailableOperators();
        String leastLoaded = null;
        int minLoad = Integer.MAX_VALUE;

        for (Operator op : operators) {
            int load = availabilityService.getOperatorLoad(op.getId());
            if (load < minLoad) {
                minLoad = load;
                leastLoaded = op.getId();
            }
        }

        return leastLoaded;
    }

    private Operator findOperator(List<Operator> operators, String operatorId) {
        for (Operator op : operators) {
            if (op.getId().equals(operatorId)) {
                return op;
            }
        }
        return null;
    }
}
