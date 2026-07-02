package com.example.telecom.notification.dto;

public class SendResult {
    private boolean success;
    private String channel;
    private String messageId;
    private String error;

    public SendResult() {}

    public SendResult(boolean success, String channel, String messageId, String error) {
        this.success = success;
        this.channel = channel;
        this.messageId = messageId;
        this.error = error;
    }

    public static SendResult ok(String channel, String messageId) {
        return new SendResult(true, channel, messageId, null);
    }

    public static SendResult fail(String channel, String error) {
        return new SendResult(false, channel, null, error);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
