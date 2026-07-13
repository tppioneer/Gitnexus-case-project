package com.example.telecom.workorder.repository;

import com.example.telecom.workorder.domain.WorkOrderComment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkOrderCommentRepository {
    private final Map<String, WorkOrderComment> comments = new ConcurrentHashMap<>();

    public WorkOrderComment save(WorkOrderComment comment) {
        comments.put(comment.getCommentId(), comment);
        return comment;
    }

    public Optional<WorkOrderComment> findById(String commentId) {
        return Optional.ofNullable(comments.get(commentId));
    }

    public List<WorkOrderComment> findByWorkOrderId(String workOrderId) {
        return comments.values().stream()
                .filter(c -> workOrderId.equals(c.getWorkOrderId()))
                .toList();
    }

    public List<WorkOrderComment> findAll() {
        return new ArrayList<>(comments.values());
    }

    public boolean deleteById(String commentId) {
        return comments.remove(commentId) != null;
    }

    public long count() {
        return comments.size();
    }

    public long countByWorkOrderId(String workOrderId) {
        return comments.values().stream()
                .filter(c -> workOrderId.equals(c.getWorkOrderId()))
                .count();
    }

    public boolean deleteByWorkOrderId(String workOrderId) {
        return comments.values().removeIf(c -> workOrderId.equals(c.getWorkOrderId()));
    }

    public List<WorkOrderComment> findByWorkOrderIdAndAuthor(String workOrderId, String author) {
        return comments.values().stream()
                .filter(c -> workOrderId.equals(c.getWorkOrderId())
                        && author.equals(c.getAuthor()))
                .toList();
    }
}
