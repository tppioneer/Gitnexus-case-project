package com.example.telecom.common.event;

import com.example.telecom.common.alarm.AlarmEvent;

/**
 * Consumer interface for alarm events.
 * Implemented by workorder-service to consume alarm events.
 */
public interface AlarmEventConsumer {
    void onAlarmCreated(AlarmEvent event);
}
