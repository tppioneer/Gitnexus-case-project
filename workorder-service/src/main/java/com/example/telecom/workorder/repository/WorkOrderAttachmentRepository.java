package com.example.telecom.workorder.repository;

import com.example.telecom.workorder.domain.WorkOrderAttachment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkOrderAttachmentRepository {
    private final Map<String, WorkOrderAttachment> attachments = new ConcurrentHashMap<>();

    public WorkOrderAttachment save(WorkOrderAttachment attachment) {
        attachments.put(attachment.getAttachmentId(), attachment);
        return attachment;
    }

    public Optional<WorkOrderAttachment> findById(String attachmentId) {
        return Optional.ofNullable(attachments.get(attachmentId));
    }

    public List<WorkOrderAttachment> findByWorkOrderId(String workOrderId) {
        return attachments.values().stream()
                .filter(a -> workOrderId.equals(a.getWorkOrderId()))
                .toList();
    }

    public List<WorkOrderAttachment> findAll() {
        return new ArrayList<>(attachments.values());
    }

    public boolean deleteById(String attachmentId) {
        return attachments.remove(attachmentId) != null;
    }
}
