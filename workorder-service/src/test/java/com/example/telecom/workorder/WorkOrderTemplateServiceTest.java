package com.example.telecom.workorder;

import com.example.telecom.workorder.domain.WorkOrderTemplate;
import com.example.telecom.workorder.dto.WorkOrderTemplateRequest;
import com.example.telecom.workorder.dto.WorkOrderTemplateResponse;
import com.example.telecom.workorder.repository.WorkOrderTemplateRepository;
import com.example.telecom.workorder.service.WorkOrderTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderTemplateServiceTest {

    private WorkOrderTemplateService templateService;
    private WorkOrderTemplateRepository templateRepository;

    @BeforeEach
    void setUp() {
        templateRepository = new WorkOrderTemplateRepository();
        templateService = new WorkOrderTemplateService(templateRepository);
    }

    @Test
    void shouldCreateTemplate() {
        WorkOrderTemplateRequest request = new WorkOrderTemplateRequest();
        request.setName("Fiber Optic Repair");
        request.setDescription("Template for fiber optic cable repairs");
        request.setCategoryId("cat-fiber");
        request.setPriority("HIGH");
        request.setDefaultTitle("Fiber Repair");
        request.setDefaultDescription("Standard fiber optic repair procedure");
        request.setEstimatedDurationMinutes(120);
        request.setActive(true);

        WorkOrderTemplateResponse response = templateService.createTemplate(request);
        assertNotNull(response);
        assertNotNull(response.getTemplateId());
        assertEquals("Fiber Optic Repair", response.getName());
        assertTrue(response.isActive());
    }

    @Test
    void shouldListTemplates() {
        WorkOrderTemplateRequest request1 = new WorkOrderTemplateRequest();
        request1.setName("Template A");
        request1.setDescription("First template");
        request1.setCategoryId("cat-1");
        request1.setPriority("HIGH");
        request1.setActive(true);
        templateService.createTemplate(request1);

        WorkOrderTemplateRequest request2 = new WorkOrderTemplateRequest();
        request2.setName("Template B");
        request2.setDescription("Second template");
        request2.setCategoryId("cat-2");
        request2.setPriority("MEDIUM");
        request2.setActive(true);
        templateService.createTemplate(request2);

        List<WorkOrderTemplateResponse> templates = templateService.listTemplates();
        assertNotNull(templates);
        assertEquals(2, templates.size());
    }

    @Test
    void shouldUpdateTemplate() {
        WorkOrderTemplateRequest createRequest = new WorkOrderTemplateRequest();
        createRequest.setName("Original Name");
        createRequest.setDescription("Original description");
        createRequest.setCategoryId("cat-1");
        createRequest.setPriority("LOW");
        createRequest.setActive(true);

        WorkOrderTemplateResponse created = templateService.createTemplate(createRequest);
        assertNotNull(created.getTemplateId());

        WorkOrderTemplateRequest updateRequest = new WorkOrderTemplateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated description");
        updateRequest.setCategoryId("cat-1");
        updateRequest.setPriority("HIGH");
        updateRequest.setActive(false);

        WorkOrderTemplateResponse updated = templateService.updateTemplate(created.getTemplateId(), updateRequest);
        assertNotNull(updated);
        assertEquals("Updated Name", updated.getName());
        assertEquals("HIGH", updated.getPriority());
        assertFalse(updated.isActive());
    }

    @Test
    void shouldDeleteTemplate() {
        WorkOrderTemplateRequest request = new WorkOrderTemplateRequest();
        request.setName("Template to Delete");
        request.setDescription("Will be deleted");
        request.setCategoryId("cat-del");
        request.setPriority("LOW");
        request.setActive(true);

        WorkOrderTemplateResponse created = templateService.createTemplate(request);
        assertNotNull(created.getTemplateId());

        templateService.deleteTemplate(created.getTemplateId());

        List<WorkOrderTemplateResponse> templates = templateService.listTemplates();
        assertTrue(templates.isEmpty());
    }

    @Test
    void shouldGetActiveTemplates() {
        WorkOrderTemplateRequest activeRequest = new WorkOrderTemplateRequest();
        activeRequest.setName("Active Template");
        activeRequest.setDescription("Active");
        activeRequest.setCategoryId("cat-1");
        activeRequest.setPriority("HIGH");
        activeRequest.setActive(true);
        templateService.createTemplate(activeRequest);

        WorkOrderTemplateRequest inactiveRequest = new WorkOrderTemplateRequest();
        inactiveRequest.setName("Inactive Template");
        inactiveRequest.setDescription("Inactive");
        inactiveRequest.setCategoryId("cat-2");
        inactiveRequest.setPriority("LOW");
        inactiveRequest.setActive(false);
        templateService.createTemplate(inactiveRequest);

        List<WorkOrderTemplateResponse> active = templateService.getActiveTemplates();
        assertEquals(1, active.size());
        assertTrue(active.get(0).isActive());
    }

    @Test
    void shouldApplyTemplate() {
        WorkOrderTemplateRequest request = new WorkOrderTemplateRequest();
        request.setName("Apply Test");
        request.setDescription("Testing apply");
        request.setCategoryId("cat-1");
        request.setPriority("CRITICAL");
        request.setDefaultTitle("Emergency Repair");
        request.setEstimatedDurationMinutes(60);
        request.setActive(true);

        WorkOrderTemplateResponse created = templateService.createTemplate(request);

        WorkOrderTemplate applied = templateService.applyTemplate(created.getTemplateId());
        assertNotNull(applied);
        assertEquals("Apply Test", applied.getName());
    }

    @Test
    void shouldGetTemplateStats() {
        WorkOrderTemplateRequest request1 = new WorkOrderTemplateRequest();
        request1.setName("Stats Test 1");
        request1.setDescription("Stats 1");
        request1.setCategoryId("cat-1");
        request1.setPriority("HIGH");
        request1.setActive(true);
        templateService.createTemplate(request1);

        WorkOrderTemplateRequest request2 = new WorkOrderTemplateRequest();
        request2.setName("Stats Test 2");
        request2.setDescription("Stats 2");
        request2.setCategoryId("cat-2");
        request2.setPriority("MEDIUM");
        request2.setActive(false);
        templateService.createTemplate(request2);

        Map<String, Object> stats = templateService.getTemplateStats();
        assertEquals(2L, stats.get("total"));
        assertEquals(1L, stats.get("active"));
        assertEquals(1L, stats.get("inactive"));
    }

    @Test
    void shouldDuplicateTemplate() {
        WorkOrderTemplateRequest request = new WorkOrderTemplateRequest();
        request.setName("Original");
        request.setDescription("Original description");
        request.setCategoryId("cat-1");
        request.setPriority("HIGH");
        request.setActive(true);

        WorkOrderTemplateResponse created = templateService.createTemplate(request);

        WorkOrderTemplateResponse duplicate = templateService.duplicateTemplate(created.getTemplateId());
        assertNotNull(duplicate);
        assertTrue(duplicate.getName().contains("(Copy)"));
        assertFalse(duplicate.isActive());
    }

    @Test
    void shouldActivateAndDeactivateTemplate() {
        WorkOrderTemplateRequest request = new WorkOrderTemplateRequest();
        request.setName("Toggle Test");
        request.setDescription("Testing toggle");
        request.setCategoryId("cat-1");
        request.setPriority("LOW");
        request.setActive(false);

        WorkOrderTemplateResponse created = templateService.createTemplate(request);
        assertFalse(created.isActive());

        WorkOrderTemplateResponse activated = templateService.activateTemplate(created.getTemplateId());
        assertTrue(activated.isActive());

        WorkOrderTemplateResponse deactivated = templateService.deactivateTemplate(created.getTemplateId());
        assertFalse(deactivated.isActive());
    }
}
