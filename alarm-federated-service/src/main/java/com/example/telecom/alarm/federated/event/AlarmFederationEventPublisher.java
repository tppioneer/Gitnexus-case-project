package com.example.telecom.alarm.federated.event;

import com.example.telecom.alarm.federated.FederatedAlarmRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AlarmFederationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AlarmFederationEventPublisher.class);
    private final List<FederatedAlarmEvent> publishedEvents = new ArrayList<>();

    public void publishAlarmIngested(FederatedAlarmRecord record) {
        FederatedAlarmEvent event = new FederatedAlarmEvent(
                record.getAlarmId(),
                "ALARM_INGESTED",
                record.getSourceId(),
                record.getSeverity(),
                record.getRegionCode(),
                record.getAlarmTime()
        );
        publishedEvents.add(event);
        log.info("Published alarm ingested event: {}", event.getEventType());
    }

    public void publishAlarmCorrelated(FederatedAlarmRecord record, String groupId) {
        FederatedAlarmEvent event = new FederatedAlarmEvent(
                record.getAlarmId(),
                "ALARM_CORRELATED",
                record.getSourceId(),
                record.getSeverity(),
                groupId,
                record.getAlarmTime()
        );
        publishedEvents.add(event);
        log.info("Published alarm correlated event: {} group={}", event.getEventType(), groupId);
    }

    public void publishAlarmEscalated(FederatedAlarmRecord record, String level) {
        FederatedAlarmEvent event = new FederatedAlarmEvent(
                record.getAlarmId(),
                "ALARM_ESCALATED",
                level,
                record.getSeverity(),
                record.getRegionCode(),
                record.getAlarmTime()
        );
        publishedEvents.add(event);
        log.info("Published alarm escalated event: {} level={}", event.getEventType(), level);
    }

    public void publishFederationEvent(FederatedAlarmRecord record, String eventType) {
        FederatedAlarmEvent event = new FederatedAlarmEvent(
                record.getAlarmId(),
                eventType,
                record.getSourceId(),
                record.getSeverity(),
                record.getRegionCode(),
                record.getAlarmTime()
        );
        publishedEvents.add(event);
        log.info("Published federation event: {}", eventType);
    }

    public List<FederatedAlarmEvent> getPublishedEvents() {
        return List.copyOf(publishedEvents);
    }

    public void clear() {
        publishedEvents.clear();
    }
}
