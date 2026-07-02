package com.example.telecom.notification.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.notification.dto.NotificationResponse;
import com.example.telecom.notification.mapper.NotificationMapper;
import com.example.telecom.notification.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationRepository notificationRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping("/{workOrderId}")
    public ApiResponse<List<NotificationResponse>> getByWorkOrder(@PathVariable String workOrderId) {
        return ApiResponse.success(notificationRepository.findByWorkOrderId(workOrderId).stream()
                .map(notificationMapper::toResponse).toList());
    }
}
