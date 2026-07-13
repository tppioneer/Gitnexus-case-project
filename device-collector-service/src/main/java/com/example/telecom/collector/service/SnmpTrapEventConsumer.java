package com.example.telecom.collector.service;

import com.example.telecom.collector.dto.NormalizedAlarm;

import java.util.ArrayList;
import java.util.List;

public class SnmpTrapEventConsumer {

    private final List<SnmpTrapEvent> eventLog = new ArrayList<>();

    public void onTrapReceived(SnmpTrapEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Trap event must not be null");
        }
        event.setPhase("RECEIVED");
        event.setProcessedAt(System.currentTimeMillis());
        eventLog.add(event);
    }

    public void onTrapProcessed(SnmpTrapEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Trap event must not be null");
        }
        event.setPhase("PROCESSED");
        event.setProcessedAt(System.currentTimeMillis());
        eventLog.add(event);
    }

    public void onTrapError(SnmpTrapEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Trap event must not be null");
        }
        event.setPhase("ERROR");
        event.setProcessedAt(System.currentTimeMillis());
        eventLog.add(event);
    }

    public List<SnmpTrapEvent> getEventLog() {
        return new ArrayList<>(eventLog);
    }

    public void clear() {
        eventLog.clear();
    }

    public static class SnmpTrapEvent {
        private final String trapId;
        private final String vendor;
        private final String rawData;
        private NormalizedAlarm normalizedAlarm;
        private String phase;
        private long receivedAt;
        private long processedAt;
        private String errorMessage;

        public SnmpTrapEvent(String trapId, String vendor, String rawData, long receivedAt) {
            this.trapId = trapId;
            this.vendor = vendor;
            this.rawData = rawData;
            this.receivedAt = receivedAt;
            this.phase = "INITIAL";
        }

        public String getTrapId() { return trapId; }
        public String getVendor() { return vendor; }
        public String getRawData() { return rawData; }
        public NormalizedAlarm getNormalizedAlarm() { return normalizedAlarm; }
        public void setNormalizedAlarm(NormalizedAlarm normalizedAlarm) { this.normalizedAlarm = normalizedAlarm; }
        public String getPhase() { return phase; }
        public void setPhase(String phase) { this.phase = phase; }
        public long getReceivedAt() { return receivedAt; }
        public long getProcessedAt() { return processedAt; }
        public void setProcessedAt(long processedAt) { this.processedAt = processedAt; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
