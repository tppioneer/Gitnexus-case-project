package com.example.telecom.notification.domain;

public class FailedNotification {

    private String failureId;
    private String notificationId;
    private String channel;
    private String recipient;
    private String subject;
    private String body;
    private String errorMessage;
    private int attemptCount;
    private long lastAttemptTime;
    private long createdAt;

    public FailedNotification() {
    }

    public FailedNotification(String failureId, String notificationId, String channel,
                              String recipient, String subject, String body,
                              String errorMessage, int attemptCount,
                              long lastAttemptTime, long createdAt) {
        this.failureId = failureId;
        this.notificationId = notificationId;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.errorMessage = errorMessage;
        this.attemptCount = attemptCount;
        this.lastAttemptTime = lastAttemptTime;
        this.createdAt = createdAt;
    }

    public String getFailureId() {
        return failureId;
    }

    public void setFailureId(String failureId) {
        this.failureId = failureId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public long getLastAttemptTime() {
        return lastAttemptTime;
    }

    public void setLastAttemptTime(long lastAttemptTime) {
        this.lastAttemptTime = lastAttemptTime;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
