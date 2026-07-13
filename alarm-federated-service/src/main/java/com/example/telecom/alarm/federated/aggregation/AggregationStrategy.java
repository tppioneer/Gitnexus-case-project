package com.example.telecom.alarm.federated.aggregation;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import java.util.List;

public interface AggregationStrategy {

    AggregationResult aggregate(List<FederatedAlarmRecord> alarms);

    String getType();
}
