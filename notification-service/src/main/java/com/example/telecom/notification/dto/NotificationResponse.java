package com.example.telecom.notification.dto;

public class NotificationResponse {
    private String recordId;
    private String workOrderId;
    private String channel;
    private String subject;
    private boolean sent;
    private long sentTime;

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }
    public long getSentTime() { return sentTime; }
    public void setSentTime(long sentTime) { this.sentTime = sentTime; }
}
