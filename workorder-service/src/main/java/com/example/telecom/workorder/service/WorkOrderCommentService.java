package com.example.telecom.workorder.service;

import com.example.telecom.workorder.domain.WorkOrderComment;
import com.example.telecom.workorder.dto.WorkOrderCommentRequest;
import com.example.telecom.workorder.dto.WorkOrderCommentResponse;
import com.example.telecom.workorder.repository.WorkOrderCommentRepository;
import com.example.telecom.workorder.repository.WorkOrderRepository;

import java.util.*;
import java.util.stream.Collectors;

public class WorkOrderCommentService {

    private final WorkOrderCommentRepository commentRepository;
    private final WorkOrderRepository workOrderRepository;

    public WorkOrderCommentService(WorkOrderCommentRepository commentRepository,
                                   WorkOrderRepository workOrderRepository) {
        this.commentRepository = commentRepository;
        this.workOrderRepository = workOrderRepository;
    }

    public WorkOrderCommentResponse addComment(WorkOrderCommentRequest request) {
        if (workOrderRepository.findById(request.getWorkOrderId()).isEmpty()) {
            throw new NoSuchElementException("Work order not found: " + request.getWorkOrderId());
        }

        String commentId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        WorkOrderComment comment = new WorkOrderComment(
                commentId,
                request.getWorkOrderId(),
                request.getAuthor(),
                request.getContent(),
                request.isInternal(),
                now
        );

        commentRepository.save(comment);
        return mapToResponse(comment);
    }

    public Optional<WorkOrderCommentResponse> getComment(String commentId) {
        return commentRepository.findById(commentId)
                .map(this::mapToResponse);
    }

    public List<WorkOrderCommentResponse> getCommentsByWorkOrderId(String workOrderId) {
        return commentRepository.findByWorkOrderId(workOrderId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean deleteComment(String commentId) {
        return commentRepository.deleteById(commentId);
    }

    public Optional<WorkOrderCommentResponse> updateComment(String commentId, String newContent) {
        return commentRepository.findById(commentId)
                .map(comment -> {
                    comment.setContent(newContent);
                    commentRepository.save(comment);
                    return mapToResponse(comment);
                });
    }

    public long countCommentsByWorkOrder(String workOrderId) {
        return commentRepository.countByWorkOrderId(workOrderId);
    }

    public List<WorkOrderCommentResponse> getPublicCommentsByWorkOrder(String workOrderId) {
        return commentRepository.findByWorkOrderId(workOrderId).stream()
                .filter(c -> !c.isInternal())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<WorkOrderCommentResponse> getInternalCommentsByWorkOrder(String workOrderId) {
        return commentRepository.findByWorkOrderId(workOrderId).stream()
                .filter(WorkOrderComment::isInternal)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<WorkOrderCommentResponse> getCommentsByAuthor(String author) {
        return commentRepository.findAll().stream()
                .filter(c -> author.equals(c.getAuthor()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<WorkOrderCommentResponse> getRecentComments(int limit) {
        return commentRepository.findAll().stream()
                .sorted(Comparator.comparingLong(WorkOrderComment::getCreatedTime).reversed())
                .limit(limit)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCommentAnalytics(String workOrderId) {
        List<WorkOrderComment> comments = commentRepository.findByWorkOrderId(workOrderId);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalComments", (long) comments.size());
        analytics.put("internalComments", comments.stream().filter(WorkOrderComment::isInternal).count());
        analytics.put("publicComments", comments.stream().filter(c -> !c.isInternal()).count());
        analytics.put("uniqueAuthors", comments.stream().map(WorkOrderComment::getAuthor).distinct().count());
        analytics.put("latestCommentTime", comments.stream()
                .mapToLong(WorkOrderComment::getCreatedTime)
                .max()
                .orElse(0L));
        return analytics;
    }

    public boolean deleteCommentsByWorkOrder(String workOrderId) {
        commentRepository.deleteByWorkOrderId(workOrderId);
        return true;
    }

    public List<WorkOrderCommentResponse> searchComments(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return commentRepository.findAll().stream()
                .filter(c -> c.getContent().toLowerCase().contains(lowerKeyword))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<WorkOrderCommentResponse> getCommentsSince(long timestamp) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getCreatedTime() >= timestamp)
                .sorted(Comparator.comparingLong(WorkOrderComment::getCreatedTime).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private WorkOrderCommentResponse mapToResponse(WorkOrderComment comment) {
        WorkOrderCommentResponse response = new WorkOrderCommentResponse();
        response.setCommentId(comment.getCommentId());
        response.setWorkOrderId(comment.getWorkOrderId());
        response.setAuthor(comment.getAuthor());
        response.setContent(comment.getContent());
        response.setInternal(comment.isInternal());
        response.setCreatedTime(comment.getCreatedTime());
        return response;
    }
}
