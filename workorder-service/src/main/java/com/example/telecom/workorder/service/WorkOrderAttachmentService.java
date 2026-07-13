package com.example.telecom.workorder.service;

import com.example.telecom.workorder.domain.WorkOrderAttachment;
import com.example.telecom.workorder.repository.WorkOrderAttachmentRepository;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.*;
import java.util.stream.Collectors;

public class WorkOrderAttachmentService {

    private final WorkOrderAttachmentRepository attachmentRepository;
    private final WorkOrderRepository workOrderRepository;

    public WorkOrderAttachmentService(WorkOrderAttachmentRepository attachmentRepository,
                                      WorkOrderRepository workOrderRepository) {
        this.attachmentRepository = attachmentRepository;
        this.workOrderRepository = workOrderRepository;
    }

    public WorkOrderAttachment addAttachment(String workOrderId, String fileName, long fileSize,
                                              String contentType, String storagePath, String uploadedBy) {
        if (workOrderRepository.findById(workOrderId).isEmpty()) {
            throw new NoSuchElementException("Work order not found: " + workOrderId);
        }

        String attachmentId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        WorkOrderAttachment attachment = new WorkOrderAttachment(
                attachmentId,
                workOrderId,
                fileName,
                fileSize,
                contentType,
                storagePath,
                uploadedBy,
                now
        );

        attachmentRepository.save(attachment);
        return attachment;
    }

    public Optional<WorkOrderAttachment> getAttachment(String attachmentId) {
        return attachmentRepository.findById(attachmentId);
    }

    public List<WorkOrderAttachment> getAttachmentsByWorkOrderId(String workOrderId) {
        return attachmentRepository.findByWorkOrderId(workOrderId);
    }

    public boolean deleteAttachment(String attachmentId) {
        return attachmentRepository.deleteById(attachmentId);
    }

    public long getTotalAttachmentSize(String workOrderId) {
        return attachmentRepository.findByWorkOrderId(workOrderId).stream()
                .mapToLong(WorkOrderAttachment::getFileSize)
                .sum();
    }

    public long countAttachmentsByWorkOrderId(String workOrderId) {
        return attachmentRepository.findByWorkOrderId(workOrderId).size();
    }

    public List<WorkOrderAttachment> getAttachmentsByFileType(String workOrderId, String contentType) {
        return attachmentRepository.findByWorkOrderId(workOrderId).stream()
                .filter(a -> contentType.equals(a.getContentType()))
                .collect(Collectors.toList());
    }

    public List<WorkOrderAttachment> getAttachmentsByUploader(String workOrderId, String uploadedBy) {
        return attachmentRepository.findByWorkOrderId(workOrderId).stream()
                .filter(a -> uploadedBy.equals(a.getUploadedBy()))
                .collect(Collectors.toList());
    }

    public List<WorkOrderAttachment> searchAttachments(String fileNameKeyword) {
        String lowerKeyword = fileNameKeyword.toLowerCase();
        return attachmentRepository.findAll().stream()
                .filter(a -> a.getFileName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getAttachmentStatsByWorkOrder(String workOrderId) {
        List<WorkOrderAttachment> attachments = attachmentRepository.findByWorkOrderId(workOrderId);
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalCount", (long) attachments.size());
        stats.put("totalSize", attachments.stream().mapToLong(WorkOrderAttachment::getFileSize).sum());
        stats.put("averageSize", attachments.isEmpty() ? 0L :
                attachments.stream().mapToLong(WorkOrderAttachment::getFileSize).sum() / attachments.size());
        return stats;
    }

    public boolean deleteAllAttachments(String workOrderId) {
        List<WorkOrderAttachment> attachments = attachmentRepository.findByWorkOrderId(workOrderId);
        for (WorkOrderAttachment attachment : attachments) {
            attachmentRepository.deleteById(attachment.getAttachmentId());
        }
        return true;
    }

    public List<WorkOrderAttachment> getRecentAttachments(int limit) {
        return attachmentRepository.findAll().stream()
                .sorted(Comparator.comparingLong(WorkOrderAttachment::getCreatedTime).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Optional<WorkOrderAttachment> findAttachmentByName(String workOrderId, String fileName) {
        return attachmentRepository.findByWorkOrderId(workOrderId).stream()
                .filter(a -> fileName.equals(a.getFileName()))
                .findFirst();
    }

    public List<String> getDistinctContentTypes() {
        return attachmentRepository.findAll().stream()
                .map(WorkOrderAttachment::getContentType)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
