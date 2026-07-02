package com.example.telecom.notification.template;

public class NotificationRecord {
    private String recordId;
    private String workOrderId;
    private String channel;
    private String recipient;
    private String subject;
    private String body;
    private boolean sent;
    private long sentTime;

    public NotificationRecord() {}

    public NotificationRecord(String recordId, String workOrderId, String channel, String recipient,
                               String subject, String body, boolean sent, long sentTime) {
        this.recordId = recordId;
        this.workOrderId = workOrderId;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.sent = sent;
        this.sentTime = sentTime;
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(String workOrderId) { this.workOrderId = workOrderId; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }
    public long getSentTime() { return sentTime; }
    public void setSentTime(long sentTime) { this.sentTime = sentTime; }
}
