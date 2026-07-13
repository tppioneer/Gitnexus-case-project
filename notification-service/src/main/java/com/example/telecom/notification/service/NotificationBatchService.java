package com.example.telecom.notification.service;

import com.example.telecom.notification.domain.NotificationMessage;
import com.example.telecom.notification.dto.NotificationBatchRequest;
import com.example.telecom.notification.dto.NotificationBatchResponse;
import com.example.telecom.notification.dto.SendResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationBatchService {

    private final NotificationRetryService retryService;
    private final List<NotificationBatchResponse> batchHistory = new ArrayList<>();

    public NotificationBatchService(NotificationRetryService retryService) {
        this.retryService = retryService;
    }

    public NotificationBatchResponse sendBatch(NotificationBatchRequest request) {
        if (request == null || request.getRecipients() == null || request.getRecipients().isEmpty()) {
            NotificationBatchResponse emptyResponse = new NotificationBatchResponse();
            emptyResponse.setBatchId(request != null ? request.getBatchId() : UUID.randomUUID().toString());
            emptyResponse.setChannel(request != null ? request.getChannel() : "UNKNOWN");
            emptyResponse.setTotalCount(0);
            emptyResponse.setSuccessCount(0);
            emptyResponse.setFailureCount(0);
            emptyResponse.setStatus("COMPLETED");
            emptyResponse.setCompletedAt(System.currentTimeMillis());
            batchHistory.add(emptyResponse);
            return emptyResponse;
        }

        String batchId = request.getBatchId() != null ? request.getBatchId() : UUID.randomUUID().toString();
        int successCount = 0;
        int failureCount = 0;

        for (String recipient : request.getRecipients()) {
            NotificationMessage message = new NotificationMessage(
                    UUID.randomUUID().toString(),
                    request.getChannel(),
                    recipient,
                    request.getSubject(),
                    request.getBody(),
                    request.getPriority(),
                    System.currentTimeMillis()
            );

            boolean sent = sendSingle(message);
            if (sent) {
                successCount++;
            } else {
                failureCount++;
                retryService.recordFailure(
                        message.getMessageId(),
                        message.getChannel(),
                        message.getRecipient(),
                        message.getSubject(),
                        message.getBody(),
                        "Failed to send batch notification"
                );
            }
        }

        NotificationBatchResponse response = new NotificationBatchResponse(
                batchId,
                request.getChannel(),
                request.getRecipients().size(),
                successCount,
                failureCount,
                "COMPLETED",
                System.currentTimeMillis()
        );

        batchHistory.add(response);
        return response;
    }

    private boolean sendSingle(NotificationMessage message) {
        if (message == null) {
            return false;
        }
        if (message.getRecipient() == null || message.getRecipient().trim().isEmpty()) {
            return false;
        }
        if (message.getBody() == null || message.getBody().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public NotificationBatchResponse getBatchStatus(String batchId) {
        if (batchId == null) {
            NotificationBatchResponse response = new NotificationBatchResponse();
            response.setStatus("NOT_FOUND");
            return response;
        }
        return batchHistory.stream()
                .filter(b -> batchId.equals(b.getBatchId()))
                .findFirst()
                .orElseGet(() -> {
                    NotificationBatchResponse response = new NotificationBatchResponse();
                    response.setBatchId(batchId);
                    response.setStatus("NOT_FOUND");
                    return response;
                });
    }

    public List<NotificationBatchResponse> getBatchHistory() {
        return new ArrayList<>(batchHistory);
    }

    public long getTotalBatchesSent() {
        return batchHistory.size();
    }

    public long getTotalNotificationsSent() {
        return batchHistory.stream().mapToLong(NotificationBatchResponse::getTotalCount).sum();
    }
}
