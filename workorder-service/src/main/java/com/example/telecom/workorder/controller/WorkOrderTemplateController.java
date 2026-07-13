package com.example.telecom.workorder.controller;

import com.example.telecom.common.api.ApiResponse;
import com.example.telecom.workorder.dto.WorkOrderTemplateRequest;
import com.example.telecom.workorder.dto.WorkOrderTemplateResponse;
import com.example.telecom.workorder.service.WorkOrderTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workorders/templates")
public class WorkOrderTemplateController {

    private final WorkOrderTemplateService workOrderTemplateService;

    public WorkOrderTemplateController(WorkOrderTemplateService workOrderTemplateService) {
        this.workOrderTemplateService = workOrderTemplateService;
    }

    @PostMapping
    public ApiResponse<WorkOrderTemplateResponse> createTemplate(@RequestBody WorkOrderTemplateRequest request) {
        return ApiResponse.success(workOrderTemplateService.createTemplate(request));
    }

    @GetMapping
    public ApiResponse<List<WorkOrderTemplateResponse>> listTemplates(
            @RequestParam(required = false) Boolean active) {
        if (Boolean.TRUE.equals(active)) {
            return ApiResponse.success(workOrderTemplateService.getActiveTemplates());
        }
        return ApiResponse.success(workOrderTemplateService.listTemplates());
    }

    @GetMapping("/{templateId}")
    public ApiResponse<WorkOrderTemplateResponse> getTemplate(@PathVariable String templateId) {
        return workOrderTemplateService.getTemplate(templateId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Template not found"));
    }

    @PutMapping("/{templateId}")
    public ApiResponse<WorkOrderTemplateResponse> updateTemplate(
            @PathVariable String templateId,
            @RequestBody WorkOrderTemplateRequest request) {
        return ApiResponse.success(workOrderTemplateService.updateTemplate(templateId, request));
    }

    @DeleteMapping("/{templateId}")
    public ApiResponse<Void> deleteTemplate(@PathVariable String templateId) {
        boolean deleted = workOrderTemplateService.deleteTemplate(templateId);
        if (deleted) {
            return ApiResponse.success(null);
        }
        return ApiResponse.error(404, "Template not found");
    }
}
