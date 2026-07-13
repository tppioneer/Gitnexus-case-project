package com.example.telecom.notification.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.notification.dto.NotificationBatchRequest;
import com.example.telecom.notification.dto.NotificationBatchResponse;
import com.example.telecom.notification.service.NotificationBatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/batch")
public class NotificationBatchController {

    private final NotificationBatchService notificationBatchService;

    public NotificationBatchController(NotificationBatchService notificationBatchService) {
        this.notificationBatchService = notificationBatchService;
    }

    @PostMapping
    public ApiResponse<NotificationBatchResponse> sendBatch(@RequestBody NotificationBatchRequest request) {
        if (request == null) {
            return ApiResponse.error(400, "Request body must not be null");
        }
        if (request.getChannel() == null || request.getChannel().trim().isEmpty()) {
            return ApiResponse.error(400, "Channel must be specified");
        }
        if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
            return ApiResponse.error(400, "At least one recipient must be specified");
        }
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            return ApiResponse.error(400, "Subject must not be empty");
        }
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            return ApiResponse.error(400, "Body must not be empty");
        }
        NotificationBatchResponse result = notificationBatchService.sendBatch(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/{batchId}")
    public ApiResponse<NotificationBatchResponse> getBatchStatus(@PathVariable String batchId) {
        if (batchId == null || batchId.trim().isEmpty()) {
            return ApiResponse.error(400, "Batch ID must be provided");
        }
        NotificationBatchResponse result = notificationBatchService.getBatchStatus(batchId);
        if ("NOT_FOUND".equals(result.getStatus())) {
            return ApiResponse.error(404, "Batch not found: " + batchId);
        }
        return ApiResponse.success(result);
    }

    @GetMapping
    public ApiResponse<List<NotificationBatchResponse>> getBatchHistory() {
        List<NotificationBatchResponse> result = notificationBatchService.getBatchHistory();
        return ApiResponse.success(result);
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getBatchStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBatches", notificationBatchService.getTotalBatchesSent());
        stats.put("totalNotifications", notificationBatchService.getTotalNotificationsSent());
        return ApiResponse.success(stats);
    }
}
