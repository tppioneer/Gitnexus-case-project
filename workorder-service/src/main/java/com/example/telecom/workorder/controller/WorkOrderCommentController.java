package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.workorder.dto.WorkOrderCommentRequest;
import com.example.telecom.workorder.dto.WorkOrderCommentResponse;
import com.example.telecom.workorder.service.WorkOrderCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workorders/comments")
public class WorkOrderCommentController {

    private final WorkOrderCommentService workOrderCommentService;

    public WorkOrderCommentController(WorkOrderCommentService workOrderCommentService) {
        this.workOrderCommentService = workOrderCommentService;
    }

    @PostMapping
    public ApiResponse<WorkOrderCommentResponse> addComment(@RequestBody WorkOrderCommentRequest request) {
        return ApiResponse.success(workOrderCommentService.addComment(request));
    }

    @GetMapping("/{commentId}")
    public ApiResponse<WorkOrderCommentResponse> getComment(@PathVariable String commentId) {
        return workOrderCommentService.getComment(commentId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Comment not found"));
    }

    @GetMapping("/work-order/{workOrderId}")
    public ApiResponse<List<WorkOrderCommentResponse>> getCommentsByWorkOrder(@PathVariable String workOrderId) {
        return ApiResponse.success(workOrderCommentService.getCommentsByWorkOrderId(workOrderId));
    }

    @PutMapping("/{commentId}")
    public ApiResponse<WorkOrderCommentResponse> updateComment(
            @PathVariable String commentId,
            @RequestBody Map<String, String> body) {
        String content = body.get("content");
        return workOrderCommentService.updateComment(commentId, content)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Comment not found"));
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable String commentId) {
        boolean deleted = workOrderCommentService.deleteComment(commentId);
        if (deleted) {
            return ApiResponse.success(null);
        }
        return ApiResponse.error(404, "Comment not found");
    }
}
