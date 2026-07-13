package com.example.telecom.workorder.domain;

public class WorkOrderComment {

    private String commentId;
    private String workOrderId;
    private String author;
    private String content;
    private boolean isInternal;
    private long createdTime;

    public WorkOrderComment() {
    }

    public WorkOrderComment(String commentId, String workOrderId, String author,
                            String content, boolean isInternal, long createdTime) {
        this.commentId = commentId;
        this.workOrderId = workOrderId;
        this.author = author;
        this.content = content;
        this.isInternal = isInternal;
        this.createdTime = createdTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setInternal(boolean internal) {
        isInternal = internal;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
