package com.example.telecom.workorder;

import com.example.telecom.workorder.domain.WorkOrderCategory;
import com.example.telecom.workorder.dto.WorkOrderCategoryRequest;
import com.example.telecom.workorder.dto.WorkOrderCategoryResponse;
import com.example.telecom.workorder.repository.WorkOrderCategoryRepository;
import com.example.telecom.workorder.service.WorkOrderCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderCategoryServiceTest {

    private WorkOrderCategoryService categoryService;
    private WorkOrderCategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository = new WorkOrderCategoryRepository();
        categoryService = new WorkOrderCategoryService(categoryRepository);
    }

    @Test
    void shouldCreateCategory() {
        WorkOrderCategoryRequest request = new WorkOrderCategoryRequest();
        request.setName("Fiber Optic");
        request.setDescription("Fiber optic network repairs");
        request.setParentCategoryId("cat-network");
        request.setSlaResponseTimeMs(3600000);
        request.setSlaResolutionTimeMs(14400000);

        WorkOrderCategoryResponse response = categoryService.createCategory(request);
        assertNotNull(response);
        assertNotNull(response.getCategoryId());
        assertEquals("Fiber Optic", response.getName());
        assertEquals(3600000, response.getSlaResponseTimeMs());
        assertEquals(14400000, response.getSlaResolutionTimeMs());
    }

    @Test
    void shouldListCategories() {
        WorkOrderCategoryRequest request1 = new WorkOrderCategoryRequest();
        request1.setName("Category A");
        request1.setDescription("First category");
        request1.setSlaResponseTimeMs(1800000);
        request1.setSlaResolutionTimeMs(7200000);
        categoryService.createCategory(request1);

        WorkOrderCategoryRequest request2 = new WorkOrderCategoryRequest();
        request2.setName("Category B");
        request2.setDescription("Second category");
        request2.setSlaResponseTimeMs(3600000);
        request2.setSlaResolutionTimeMs(14400000);
        categoryService.createCategory(request2);

        List<WorkOrderCategoryResponse> categories = categoryService.listCategories();
        assertNotNull(categories);
        assertEquals(2, categories.size());
    }

    @Test
    void shouldUpdateCategory() {
        WorkOrderCategoryRequest createRequest = new WorkOrderCategoryRequest();
        createRequest.setName("Original Category");
        createRequest.setDescription("Original description");
        createRequest.setSlaResponseTimeMs(1800000);
        createRequest.setSlaResolutionTimeMs(7200000);

        WorkOrderCategoryResponse created = categoryService.createCategory(createRequest);
        assertNotNull(created.getCategoryId());

        WorkOrderCategoryRequest updateRequest = new WorkOrderCategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setDescription("Updated description");
        updateRequest.setParentCategoryId("cat-parent");
        updateRequest.setSlaResponseTimeMs(3600000);
        updateRequest.setSlaResolutionTimeMs(14400000);

        WorkOrderCategoryResponse updated = categoryService.updateCategory(created.getCategoryId(), updateRequest);
        assertNotNull(updated);
        assertEquals("Updated Category", updated.getName());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(3600000, updated.getSlaResponseTimeMs());
    }

    @Test
    void shouldDeleteCategory() {
        WorkOrderCategoryRequest request = new WorkOrderCategoryRequest();
        request.setName("Category to Delete");
        request.setDescription("Will be removed");
        request.setSlaResponseTimeMs(1800000);
        request.setSlaResolutionTimeMs(7200000);

        WorkOrderCategoryResponse created = categoryService.createCategory(request);
        assertNotNull(created.getCategoryId());

        categoryService.deleteCategory(created.getCategoryId());

        List<WorkOrderCategoryResponse> categories = categoryService.listCategories();
        assertTrue(categories.isEmpty());
    }

    @Test
    void shouldGetRootCategories() {
        WorkOrderCategoryRequest root = new WorkOrderCategoryRequest();
        root.setName("Root Category");
        root.setDescription("Root level");
        root.setSlaResponseTimeMs(1800000);
        root.setSlaResolutionTimeMs(7200000);
        categoryService.createCategory(root);

        WorkOrderCategoryRequest child = new WorkOrderCategoryRequest();
        child.setName("Child Category");
        child.setDescription("Child level");
        child.setParentCategoryId("some-parent");
        child.setSlaResponseTimeMs(3600000);
        child.setSlaResolutionTimeMs(14400000);
        categoryService.createCategory(child);

        List<WorkOrderCategoryResponse> roots = categoryService.getRootCategories();
        assertFalse(roots.isEmpty());
    }

    @Test
    void shouldGetCategoryHierarchy() {
        WorkOrderCategoryRequest request = new WorkOrderCategoryRequest();
        request.setName("Network");
        request.setDescription("Network category");
        request.setSlaResponseTimeMs(1800000);
        request.setSlaResolutionTimeMs(7200000);
        categoryService.createCategory(request);

        Map<String, Object> hierarchy = categoryService.getCategoryHierarchy();
        assertNotNull(hierarchy);
    }

    @Test
    void shouldCountCategories() {
        WorkOrderCategoryRequest request = new WorkOrderCategoryRequest();
        request.setName("Test Count");
        request.setDescription("For counting");
        request.setSlaResponseTimeMs(1800000);
        request.setSlaResolutionTimeMs(7200000);
        categoryService.createCategory(request);

        assertEquals(1, categoryService.countCategories());
    }

    @Test
    void shouldCheckExistsByName() {
        WorkOrderCategoryRequest request = new WorkOrderCategoryRequest();
        request.setName("Unique Name");
        request.setDescription("Description");
        request.setSlaResponseTimeMs(1800000);
        request.setSlaResolutionTimeMs(7200000);
        categoryService.createCategory(request);

        assertTrue(categoryService.existsByName("Unique Name"));
        assertFalse(categoryService.existsByName("Non Existent"));
    }

    @Test
    void shouldUpdateSlaPolicy() {
        WorkOrderCategoryRequest request = new WorkOrderCategoryRequest();
        request.setName("SLA Test");
        request.setDescription("SLA policy test");
        request.setSlaResponseTimeMs(1800000);
        request.setSlaResolutionTimeMs(7200000);

        WorkOrderCategoryResponse created = categoryService.createCategory(request);

        WorkOrderCategoryResponse updated = categoryService.updateSlaPolicy(
                created.getCategoryId(), 3600000, 14400000);

        assertEquals(3600000, updated.getSlaResponseTimeMs());
        assertEquals(14400000, updated.getSlaResolutionTimeMs());
    }
}
