package com.example.telecom.workorder.domain;

public class WorkOrderAttachment {

    private String attachmentId;
    private String workOrderId;
    private String fileName;
    private long fileSize;
    private String contentType;
    private String storagePath;
    private String uploadedBy;
    private long createdTime;

    public WorkOrderAttachment() {
    }

    public WorkOrderAttachment(String attachmentId, String workOrderId, String fileName,
                               long fileSize, String contentType, String storagePath,
                               String uploadedBy, long createdTime) {
        this.attachmentId = attachmentId;
        this.workOrderId = workOrderId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.storagePath = storagePath;
        this.uploadedBy = uploadedBy;
        this.createdTime = createdTime;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
