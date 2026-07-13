package com.example.telecom.notification.dto;

import java.util.List;

/** Request payload for sending batch notifications to multiple recipients. */

public class NotificationBatchRequest {

    private String batchId;
    private String channel;
    private List<String> recipients;
    private String subject;
    private String body;
    private String priority;

    public NotificationBatchRequest() {
    }

    public NotificationBatchRequest(String batchId, String channel, List<String> recipients,
                                    String subject, String body, String priority) {
        this.batchId = batchId;
        this.channel = channel;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.priority = priority;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
